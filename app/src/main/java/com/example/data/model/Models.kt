package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Strayer",
    val age: Int = 28,
    val lifeExpectancy: Int = 80
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val pillar: String, // "HEALTH", "WEALTH", "WISDOM", "HAPPINESS"
    val streak: Int = 0,
    val lastCompletedTimestamp: Long = 0L,
    val totalCompletions: Int = 0
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val matrixCategory: String, // "URGENT_IMPORTANT", "IMPORTANT_NOT_URGENT", "URGENT_NOT_IMPORTANT", "NOT_URGENT_NOT_IMPORTANT"
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val emoji: String, // 😂, 😐, 😔, 😡, 🥱 etc.
    val moodScore: Int, // 1 to 5
    val energyScore: Int, // 1 to 5
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
