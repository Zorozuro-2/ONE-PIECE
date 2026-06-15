package com.example.data.local

import androidx.room.*
import com.example.data.model.Habit
import com.example.data.model.MoodEntry
import com.example.data.model.Task
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface YlagDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    // Habits
    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabitsFlow(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Tasks (Eisenhower Matrix)
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun clearCompletedTasks()

    // Mood Entries (7-day trend visualization)
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMoodEntriesFlow(limit: Int = 14): Flow<List<MoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntry)
}
