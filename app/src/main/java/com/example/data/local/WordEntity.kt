package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val lemma: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val translation: String,
    val visualEmoji: String,
    val visualExplanation: String,
    val contextSentence: String,
    val partOfSpeech: String,
    val isDifficultAutoSuggested: Boolean = false,
    val masteryLevel: Int = 0, // 0 = New, 1 = Learning, 2 = Mastered
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val documentId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
