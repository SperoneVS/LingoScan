package com.example.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: JsonObject? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val imageConfig: ImageConfig? = null,
    val responseModalities: List<String>? = null
)

@Serializable
data class ImageConfig(
    val aspectRatio: String = "1:1",
    val imageSize: String = "1K"
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

// Domain representations parsed from Gemini
@Serializable
data class OcrScanResult(
    val detectedLanguageCode: String = "und",
    val detectedLanguageName: String = "Unbekannt",
    val title: String = "Gescannter Text",
    val extractedText: String = "",
    val difficultWords: List<DifficultWordSuggestion> = emptyList()
)

@Serializable
data class DifficultWordSuggestion(
    val word: String,
    val lemma: String = "",
    val partOfSpeech: String = "",
    val translation: String,
    val visualEmoji: String = "💡",
    val visualExplanation: String,
    val contextSentence: String = ""
)

@Serializable
data class WordAnalysisResult(
    val word: String,
    val lemma: String,
    val partOfSpeech: String,
    val visualEmoji: String,
    val visualExplanation: String,
    val translation: String,
    val exampleSentence: String,
    val generatedImageBase64: String? = null
)

@Serializable
data class QuizDistractorResult(
    val correctTranslation: String,
    val distractors: List<String>
)
