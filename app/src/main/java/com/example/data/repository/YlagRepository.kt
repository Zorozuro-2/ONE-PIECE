package com.example.data.repository

import com.example.data.local.YlagDao
import com.example.data.model.Habit
import com.example.data.model.MoodEntry
import com.example.data.model.Task
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class YlagRepository(private val dao: YlagDao) {

    // User Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()

    suspend fun saveUserProfile(profile: UserProfile) {
        dao.insertProfile(profile)
    }

    // Habits
    val allHabits: Flow<List<Habit>> = dao.getAllHabitsFlow()

    suspend fun addHabit(habit: Habit) {
        dao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit)
    }

    // Tasks (Eisenhower Matrix)
    val allTasks: Flow<List<Task>> = dao.getAllTasksFlow()

    suspend fun addTask(task: Task) {
        dao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        dao.deleteTask(task)
    }

    suspend fun clearCompletedTasks() {
        dao.clearCompletedTasks()
    }

    // Journal
    val recentMoodEntries: Flow<List<MoodEntry>> = dao.getRecentMoodEntriesFlow()

    suspend fun addMoodEntry(entry: MoodEntry) {
        dao.insertMoodEntry(entry)
    }

    suspend fun deleteMoodEntry(entry: MoodEntry) {
        dao.deleteMoodEntry(entry)
    }

    // Helper to seed initial data if needed
    suspend fun seedInitialDataIfNeeded() {
        val profile = dao.getUserProfile()
        if (profile == null) {
            // Seed Profile
            dao.insertProfile(UserProfile(id = 1, name = "Monkey D. Luffy", age = 19, lifeExpectancy = 100))

            // Seed initial Habits (one for each of the 4 pillars)
            dao.insertHabit(Habit(name = "Daily Conqueror Haki Training", pillar = "HEALTH", streak = 5, totalCompletions = 5))
            dao.insertHabit(Habit(name = "Amass Bounty & Gold Coins", pillar = "WEALTH", streak = 12, totalCompletions = 12))
            dao.insertHabit(Habit(name = "Translate Secret Poneglyphs", pillar = "WISDOM", streak = 7, totalCompletions = 7))
            dao.insertHabit(Habit(name = "Celebrate and drink sake with Nakama", pillar = "HAPPINESS", streak = 20, totalCompletions = 20))

            // Seed initial Tasks (Eisenhower Matrix)
            dao.insertTask(Task(title = "Defeat the Kaido Threat in Wano", matrixCategory = "URGENT_IMPORTANT", isCompleted = false))
            dao.insertTask(Task(title = "Replenish cola and meat on Thousand Terras", matrixCategory = "URGENT_NOT_IMPORTANT", isCompleted = false))
            dao.insertTask(Task(title = "Refine advanced Armament flow", matrixCategory = "IMPORTANT_NOT_URGENT", isCompleted = false))
            dao.insertTask(Task(title = "Swipe the dust off Sunny's deck", matrixCategory = "NOT_URGENT_NOT_IMPORTANT", isCompleted = true))

            // Seed initial Mood history (last 7 days) to make chart look amazing
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L
            dao.insertMoodEntry(MoodEntry(emoji = "🧘", moodScore = 5, energyScore = 4, note = "Meditated near the sea breeze. Gained clarity.", timestamp = now - 6 * dayInMillis))
            dao.insertMoodEntry(MoodEntry(emoji = "⚡", moodScore = 4, energyScore = 5, note = "Completed core combat training exercises.", timestamp = now - 5 * dayInMillis))
            dao.insertMoodEntry(MoodEntry(emoji = "😴", moodScore = 3, energyScore = 2, note = "Caught in a heavy storm. Low energy but survived.", timestamp = now - 4 * dayInMillis))
            dao.insertMoodEntry(MoodEntry(emoji = "🌱", moodScore = 5, energyScore = 4, note = "Moored at a quiet island. Explored the jungle.", timestamp = now - 3 * dayInMillis))
            dao.insertMoodEntry(MoodEntry(emoji = "🔥", moodScore = 4, energyScore = 5, note = "Secured precious treasure box from the enemy!", timestamp = now - 2 * dayInMillis))
            dao.insertMoodEntry(MoodEntry(emoji = "😌", moodScore = 4, energyScore = 3, note = "Peaceful feast on deck under the stars.", timestamp = now - 1 * dayInMillis))
        }
    }

    // Questions of the Day
    fun getQuestionOfTheDay(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % QUESTIONS.size
        return QUESTIONS[index]
    }

    companion object {
        val QUESTIONS = listOf(
            "If your adventure ended today, would you say you lived with absolute freedom?" to "Way of Luffy",
            "What mental wall is preventing you from reaching your absolute peak performance or 'Gear 5'?" to "Grand Line Mindset",
            "Are you guarding the nakama in your life with your maximum strength and conqueror Haki?" to "Crew loyalty",
            "If you were to search for your own 'All Blue', what specific milestone are you aiming to hit?" to "Dream Pursuit",
            "What standard of discipline do you refuse to compromise on, even if it means bearing a Zoro-like scar?" to "Pirate Pride",
            "Is your ambition strong enough to conquer the stormy seas of your current obstacles?" to "Conqueror's Spirit",
            "What map are you drawing for your life? Is it one you chose, or did someone else force it on you?" to "Cartography",
            "Who are you taking for granted right now who deserves a celebratory Toast of sake?" to "Nakama Toast",
            "What fears have you realized were completely imaginary once you launched your ship into the wind?" to "Courage of the Sea",
            "If you were to lose your devil fruit power, standard resources, or luck, how would you still win?" to "Inner Haki",
            "How can you bring a sense of absolute pirate joy and laughter into your hardest task today?" to "Joy Boy Spirit",
            "To withstand the storms of the Grand Line, what active habit will you use to harden your willpower?" to "Armament",
            "Are your daily habits sailing towards your eventual goal of becoming the Pirate King?" to "Dream Trajectory",
            "What is the single highest-value treasure you can claim or create today?" to "Pirate Treasure",
            "If you viewed your current daily troubles from the vast scope of the entire Red Line, how small do they seem?" to "Grand Horizon"
        )
    }
}
