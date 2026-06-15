package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Habit
import com.example.data.model.MoodEntry
import com.example.data.model.Task
import com.example.data.model.UserProfile
import com.example.data.repository.YlagRepository
import com.example.ui.components.SecurityValidator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class YlagViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = YlagRepository(db.ylagDao)

    // UI state
    val profile = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val habits = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val moodEntries = repository.recentMoodEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected Navigation Tab: "DASHBOARD", "HABITS", "TASKS", "JOURNAL", "PROFILE"
    private val _currentTab = MutableStateFlow("DASHBOARD")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Confetti flow for triggering particle effects
    private val _triggerConfetti = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val triggerConfetti: SharedFlow<Unit> = _triggerConfetti.asSharedFlow()

    // Question of the Day cache
    private val _questionOfTheDay = MutableStateFlow("" to "")
    val questionOfTheDay: StateFlow<Pair<String, String>> = _questionOfTheDay.asStateFlow()

    // Dopamine Score calculation
    val dopamineScore = combine(habits, tasks) { habitList, taskList ->
        val totalHabits = habitList.size
        val completedHabits = habitList.count { isToday(it.lastCompletedTimestamp) }

        val totalTasks = taskList.size
        val completedTasks = taskList.count { it.isCompleted }

        val totalActions = totalHabits + totalTasks
        val completedActions = completedHabits + completedTasks

        if (totalActions == 0) {
            100 // default when nothing is set
        } else {
            (completedActions * 100) / totalActions
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    val growthTip = dopamineScore.map { score ->
        if (score < 50) {
            GROWTH_TIPS.random()
        } else {
            ""
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            updateQuestion()
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun updateQuestion() {
        _questionOfTheDay.value = repository.getQuestionOfTheDay()
    }

    // --- Profile Operations ---
    fun updateProfile(name: String, age: Int, lifeExpectancy: Int) {
        viewModelScope.launch {
            val sanitizedName = SecurityValidator.sanitizeInput(name, maxLength = 50)
            val validatedAge = age.coerceIn(1, 120)
            SecurityValidator.logSecurityAttempt("UPDATE_PROFILE", true, "Name: $sanitizedName, Age: $validatedAge, Expectancy: $lifeExpectancy")
            repository.saveUserProfile(UserProfile(id = 1, name = sanitizedName, age = validatedAge, lifeExpectancy = lifeExpectancy))
        }
    }

    // --- Habit Operations ---
    fun addHabit(name: String, pillar: String) {
        viewModelScope.launch {
            val sanitizedName = SecurityValidator.sanitizeInput(name, maxLength = 80)
            val sanitizedPillar = SecurityValidator.sanitizeInput(pillar, maxLength = 20)
            SecurityValidator.logSecurityAttempt("ADD_HABIT", true, "Name: $sanitizedName, Pillar: $sanitizedPillar")
            repository.addHabit(Habit(name = sanitizedName, pillar = sanitizedPillar.uppercase()))
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val wasCompletedToday = isToday(habit.lastCompletedTimestamp)

            val updatedHabit = if (wasCompletedToday) {
                // Uncheck: decrease streak & total completions, set timestamp to 0
                val newStreak = (habit.streak - 1).coerceAtLeast(0)
                val newTotal = (habit.totalCompletions - 1).coerceAtLeast(0)
                habit.copy(streak = newStreak, lastCompletedTimestamp = 0L, totalCompletions = newTotal)
            } else {
                // Check: increase streak & total completions
                // Check if last completed was yesterday to chain streak
                val isYesterday = isYesterday(habit.lastCompletedTimestamp)
                val newStreak = if (isYesterday || habit.lastCompletedTimestamp == 0L) habit.streak + 1 else 1
                val newTotal = habit.totalCompletions + 1
                val updated = habit.copy(streak = newStreak, lastCompletedTimestamp = now, totalCompletions = newTotal)
                
                // If this check completes the last pending habit or hits high score, trigger confetti
                triggerConfettiIfHighPerformanceAfterUpdate(habitId = habit.id, isTask = false, becomingCompleted = true)

                updated
            }
            repository.updateHabit(updatedHabit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // --- Task Operations ---
    fun addTask(title: String, matrixCategory: String) {
        viewModelScope.launch {
            val sanitizedTitle = SecurityValidator.sanitizeInput(title, maxLength = 100)
            val sanitizedCategory = SecurityValidator.sanitizeInput(matrixCategory, maxLength = 30)
            SecurityValidator.logSecurityAttempt("ADD_TASK", true, "Title: $sanitizedTitle, Category: $sanitizedCategory")
            repository.addTask(Task(title = sanitizedTitle, matrixCategory = sanitizedCategory))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            if (updated.isCompleted) {
                triggerConfettiIfHighPerformanceAfterUpdate(taskId = task.id, isTask = true, becomingCompleted = true)
            }
            repository.updateTask(updated)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    // --- Mood/Journal Operations ---
    fun addMoodEntry(emoji: String, moodScore: Int, energyScore: Int, note: String) {
        viewModelScope.launch {
            val sanitizedEmoji = SecurityValidator.sanitizeInput(emoji, maxLength = 10)
            val sanitizedNote = SecurityValidator.sanitizeInput(note, maxLength = 150)
            SecurityValidator.logSecurityAttempt("ADD_MOOD", true, "Emoji: $sanitizedEmoji, Notes: ${sanitizedNote.take(15)}...")
            repository.addMoodEntry(MoodEntry(emoji = sanitizedEmoji, moodScore = moodScore, energyScore = energyScore, note = sanitizedNote))
        }
    }

    fun deleteMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            repository.deleteMoodEntry(entry)
        }
    }

    // Confetti logic helper
    private suspend fun triggerConfettiIfHighPerformanceAfterUpdate(
        habitId: Int = -1,
        taskId: Int = -1,
        isTask: Boolean,
        becomingCompleted: Boolean
    ) {
        if (!becomingCompleted) return
        
        // Let's perform a simulated calculation with the upcoming modification
        val habitList = habits.value
        val taskList = tasks.value

        val completedHabits = habitList.count { 
            if (!isTask && it.id == habitId) true else isToday(it.lastCompletedTimestamp) 
        }
        val completedTasks = taskList.count { 
            if (isTask && it.id == taskId) true else it.isCompleted 
        }

        val totalActions = habitList.size + taskList.size
        val completedActions = completedHabits + completedTasks

        val projectedScore = if (totalActions == 0) 100 else (completedActions * 100) / totalActions
        if (projectedScore >= 90) {
            _triggerConfetti.emit(Unit)
        }
    }

    // --- Data Export & Import ---
    fun exportLifeHistoryAsJson(context: Context) {
        viewModelScope.launch {
            try {
                val root = JSONObject()
                
                // Profile
                profile.value?.let { p ->
                    val jp = JSONObject()
                    jp.put("name", p.name)
                    jp.put("age", p.age)
                    jp.put("lifeExpectancy", p.lifeExpectancy)
                    root.put("profile", jp)
                }

                // Habits
                val jaHabits = JSONArray()
                habits.value.forEach { h ->
                    val jh = JSONObject()
                    jh.put("name", h.name)
                    jh.put("pillar", h.pillar)
                    jh.put("streak", h.streak)
                    jh.put("lastCompletedTimestamp", h.lastCompletedTimestamp)
                    jh.put("totalCompletions", h.totalCompletions)
                    jaHabits.put(jh)
                }
                root.put("habits", jaHabits)

                // Tasks
                val jaTasks = JSONArray()
                tasks.value.forEach { t ->
                    val jt = JSONObject()
                    jt.put("title", t.title)
                    jt.put("matrixCategory", t.matrixCategory)
                    jt.put("isCompleted", t.isCompleted)
                    jt.put("timestamp", t.timestamp)
                    jaTasks.put(jt)
                }
                root.put("tasks", jaTasks)

                // Mood
                val jaMood = JSONArray()
                moodEntries.value.forEach { m ->
                    val jm = JSONObject()
                    jm.put("emoji", m.emoji)
                    jm.put("moodScore", m.moodScore)
                    jm.put("energyScore", m.energyScore)
                    jm.put("note", m.note)
                    jm.put("timestamp", m.timestamp)
                    jaMood.put(jm)
                }
                root.put("moodEntries", jaMood)

                val jsonString = root.toString(4)

                // Share Sheet Intent
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, jsonString)
                    putExtra(Intent.EXTRA_SUBJECT, "YLAG_Life_History_Export.json")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Export YLAG Life History")
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(shareIntent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Date Utilities
    fun isToday(timestamp: Long): Boolean {
        if (timestamp == 0L) return false
        val cal1 = Calendar.getInstance()
        cal1.timeInMillis = timestamp
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(timestamp: Long): Boolean {
        if (timestamp == 0L) return false
        val cal1 = Calendar.getInstance()
        cal1.timeInMillis = timestamp
        val cal2 = Calendar.getInstance()
        cal2.add(Calendar.DAY_OF_YEAR, -1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // --- Alarm & Notification Management ---
    private val _alarmTime = MutableStateFlow("08:30")
    val alarmTime: StateFlow<String> = _alarmTime.asStateFlow()

    private val _alarmEnabledStatus = MutableStateFlow(true)
    val alarmEnabledStatus: StateFlow<Boolean> = _alarmEnabledStatus.asStateFlow()

    fun scheduleDailyAlarm(context: Context, hour: Int, minute: Int, connectToPhoneClock: Boolean) {
        viewModelScope.launch {
            val formattedHour = String.format("%02d", hour)
            val formattedMinute = String.format("%02d", minute)
            _alarmTime.value = "$formattedHour:$formattedMinute"
            _alarmEnabledStatus.value = true

            SecurityValidator.logSecurityAttempt("ALARM_SET", true, "Time: $formattedHour:$formattedMinute, ConnectToClock: $connectToPhoneClock")

            if (connectToPhoneClock) {
                // Hook directly into real Android System Alarm App/Intent Clock
                try {
                    val intent = Intent(android.provider.AlarmClock.ACTION_SET_ALARM).apply {
                        putExtra(android.provider.AlarmClock.EXTRA_HOUR, hour)
                        putExtra(android.provider.AlarmClock.EXTRA_MINUTES, minute)
                        putExtra(android.provider.AlarmClock.EXTRA_MESSAGE, "ONE PIECE HAKI TRAINING!")
                        putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, false)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    SecurityValidator.logSecurityAttempt("PHONE_CLOCK_ALARM_INTEGRATION", true, "Fired system Clock Alarm Clock Intent successfully.")
                } catch (e: Exception) {
                    Log.e("SECURITY_AUDIT", "Standard Clock app intent not found. Fallback to local scheduler.", e)
                    scheduleLocalAlarmManager(context, hour, minute)
                }
            } else {
                // Set local AlarmReceiver scheduling
                scheduleLocalAlarmManager(context, hour, minute)
            }
        }
    }

    private fun scheduleLocalAlarmManager(context: Context, hour: Int, minute: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, com.example.AlarmReceiver::class.java).apply {
                action = "com.example.ACTION_DAILY_ALARM"
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                2456,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE else 0
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            SecurityValidator.logSecurityAttempt("LOCAL_ALARM_MANAGER_SET", true, "Registered matching alarm manager at ${calendar.time}")
        } catch (e: Exception) {
            Log.e("SECURITY_AUDIT", "Failed to schedule local alarm manager", e)
        }
    }

    fun disableActiveAlarm(context: Context) {
        _alarmEnabledStatus.value = false
        SecurityValidator.logSecurityAttempt("ALARM_DISABLE", true, "Alarm disabled by operator")
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val intent = Intent(context, com.example.AlarmReceiver::class.java).apply {
                action = "com.example.ACTION_DAILY_ALARM"
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                2456,
                intent,
                android.app.PendingIntent.FLAG_NO_CREATE or if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE else 0
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        } catch (e: Exception) {
            Log.e("SECURITY_AUDIT", "Error canceling AlarmManager", e)
        }
    }

    fun triggerInstantDemoNotification(context: Context) {
        SecurityValidator.logSecurityAttempt("INSTANT_DEMO_TRIGGER", true, "Operator requested demo push notice")
        val intent = Intent(context, com.example.AlarmReceiver::class.java).apply {
            action = "com.example.ACTION_DAILY_ALARM"
        }
        context.sendBroadcast(intent)
    }

    companion object {
        val GROWTH_TIPS = listOf(
            "Health: Deep focus requires deep recovery. Standardize a consistent wind-down routine 1 hour before bed.",
            "Wealth: Compound interest applies to small habits. Redirect one small unneeded transaction to your savings today.",
            "Wisdom: Seneca asks: 'What bad habit did you cure today?' Write down one trigger you successfully avoided.",
            "Happiness: True contentment is found in being present. Look around and name 3 things that don't cost a dollar.",
            "Energy: A 5-minute cold shower or dynamic stretching routine will break you out of any afternoon brain fog.",
            "Eisenhower: Your urgent tasks are rarely important, and your important tasks are rarely urgent. Act on Importance.",
            "Philosophy: 'You could leave life right now. Let that determine what you do and say and think.' Enjoy the present.",
            "Somatic Check-in: Unclench your jaw, lower your shoulders from your ears, and take a long double inhale through your nose."
        )
    }
}
