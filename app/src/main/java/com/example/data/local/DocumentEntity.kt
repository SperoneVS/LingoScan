package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val rawText: String,
    val detectedLanguageCode: String,
    val detectedLanguageName: String,
    val difficultWordsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
