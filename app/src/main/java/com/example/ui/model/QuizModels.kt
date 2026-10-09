package com.example.ui.model

import com.example.data.local.WordEntity

enum class QuizMode {
    MULTIPLE_CHOICE,
    TEXT_INPUT
}

data class QuizQuestion(
    val wordEntity: WordEntity,
    val mode: QuizMode,
    val options: List<String> = emptyList(), // For multiple choice
    val correctTranslation: String,
    val sourceContextSentence: String = ""
)

data class QuizSessionResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val learnedWords: List<WordEntity>,
    val mode: QuizMode,
    val pointsEarned: Int = 0,
    val starsEarned: Int = 0,
    val maxStreak: Int = 0,
    val sourceTitle: String = ""
)
