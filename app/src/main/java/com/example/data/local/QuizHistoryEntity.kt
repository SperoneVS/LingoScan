package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String, // "MULTIPLE_CHOICE" or "TEXT_INPUT"
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val pointsEarned: Int,
    val starsEarned: Int,
    val accuracyPercentage: Int,
    val sourceTitle: String = ""
)
