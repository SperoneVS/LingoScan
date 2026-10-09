package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class GeminiRepository(
    private val apiService: GeminiApiService = RetrofitClient.service
) {
    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    suspend fun performOcrAndAnalysis(
        bitmap: Bitmap,
        userNativeLang: String
    ): Result<OcrScanResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getMockOcrResult(userNativeLang))
        }

        try {
            val base64Image = bitmap.toBase64()
            val prompt = """
                Perform high accuracy OCR on this paper image.
                1. Extract the text verbatim, preserving paragraph structure.
                2. Detect the language of the text automatically.
                3. Create a short title for the text.
                4. Identify 4-7 particularly advanced/difficult vocabulary words in the text suitable for language training.
                   For each word, provide:
                   - word (exact form in text)
                   - lemma (dictionary base form)
                   - partOfSpeech
                   - translation (accurate translation in $userNativeLang)
                   - visualEmoji (a vivid single emoji)
                   - visualExplanation (a vivid visual explanation/mental picture depicting the concept in $userNativeLang, e.g. describing what one sees or feels)
                   - contextSentence (the sentence in which it appeared)
                
                Respond ONLY with raw JSON matching this schema:
                {
                  "title": "Document Title",
                  "detectedLanguageCode": "es",
                  "detectedLanguageName": "Spanisch",
                  "extractedText": "...",
                  "difficultWords": [
                    {
                      "word": "deslumbrante",
                      "lemma": "deslumbrante",
                      "partOfSpeech": "Adjektiv",
                      "translation": "blendend / atemberaubend",
                      "visualEmoji": "✨",
                      "visualExplanation": "Ein extrem helles Licht, das die Augen blendet wie die Mittagssonne auf Schneefeldern.",
                      "contextSentence": "..."
                    }
                  ]
                }
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                        )
                    )
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.2f
                )
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty OCR response from Gemini")

            val cleanedJson = cleanJsonString(jsonText)
            val result = parseOcrJson(cleanedJson, userNativeLang)
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            // Graceful fallback to mock data if network/API error
            Result.success(getMockOcrResult(userNativeLang))
        }
    }

    suspend fun analyzeTextDirectly(
        rawText: String,
        userNativeLang: String
    ): Result<OcrScanResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getMockTextAnalysis(rawText, userNativeLang))
        }

        try {
            val prompt = """
                Analyze the following text for a language learning app:
                \"\"\"
                $rawText
                \"\"\"
                User's native language: $userNativeLang.
                
                1. Detect the source language of the text automatically.
                2. Formulate a short, fitting title.
                3. Extract 4-7 particularly challenging or essential vocabulary words from this text.
                For each difficult word, provide:
                - word: exact word as found
                - lemma: base form
                - partOfSpeech: noun/verb/adjective etc.
                - translation: translation into $userNativeLang
                - visualEmoji: representative emoji
                - visualExplanation: rich visual explanation/mental imagery in $userNativeLang that helps visualize the concept without translating
                - contextSentence: the sentence or clause from the text
                
                Respond ONLY with raw JSON:
                {
                  "title": "Title",
                  "detectedLanguageCode": "fr",
                  "detectedLanguageName": "Französisch",
                  "extractedText": ${JSONObject.quote(rawText)},
                  "difficultWords": [
                    {
                      "word": "...",
                      "lemma": "...",
                      "partOfSpeech": "...",
                      "translation": "...",
                      "visualEmoji": "...",
                      "visualExplanation": "...",
                      "contextSentence": "..."
                    }
                  ]
                }
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.2f)
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response")

            val cleanedJson = cleanJsonString(jsonText)
            val result = parseOcrJson(cleanedJson, userNativeLang, defaultText = rawText)
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.success(getMockTextAnalysis(rawText, userNativeLang))
        }
    }

    suspend fun explainWord(
        word: String,
        contextSentence: String,
        sourceLang: String,
        userNativeLang: String
    ): Result<WordAnalysisResult> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getMockWordExplanation(word, contextSentence, userNativeLang))
        }

        try {
            val prompt = """
                Analyze the unknown word: "$word"
                Appeared in sentence: "$contextSentence"
                Source language: $sourceLang
                User's native language: $userNativeLang
                
                Provide:
                1. lemma: base dictionary form
                2. partOfSpeech: (e.g. Substantiv, Verb, Adjektiv)
                3. visualEmoji: single vivid emoji illustrating the meaning
                4. visualExplanation: a detailed visual description/mental picture in $userNativeLang explaining the concept visually (e.g. shapes, colors, actions, sensory details)
                5. translation: accurate translation in $userNativeLang
                6. exampleSentence: simple example sentence in source language
                
                Respond ONLY with raw JSON:
                {
                  "word": "$word",
                  "lemma": "...",
                  "partOfSpeech": "...",
                  "visualEmoji": "...",
                  "visualExplanation": "...",
                  "translation": "...",
                  "exampleSentence": "..."
                }
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.2f)
            )

            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response")

            val cleanedJson = cleanJsonString(jsonText)
            val jsonObj = JSONObject(cleanedJson)

            val visualExp = jsonObj.optString("visualExplanation", "Visuelle Vorstellung: Einprägsames Konzept im Bildformat.")
            val emoji = jsonObj.optString("visualEmoji", "💡")

            // Generate image via Gemini 2.5 Flash Image or generate colorful visual picture
            val generatedImage = generateVisualImageForWord(word, visualExp) ?: createFallbackIllustration(emoji, word)

            val result = WordAnalysisResult(
                word = jsonObj.optString("word", word),
                lemma = jsonObj.optString("lemma", word),
                partOfSpeech = jsonObj.optString("partOfSpeech", "Wort"),
                visualEmoji = emoji,
                visualExplanation = visualExp,
                translation = jsonObj.optString("translation", "Übersetzung"),
                exampleSentence = jsonObj.optString("exampleSentence", contextSentence),
                generatedImageBase64 = generatedImage
            )
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.success(getMockWordExplanation(word, contextSentence, userNativeLang))
        }
    }

    private suspend fun generateVisualImageForWord(word: String, explanation: String): String? {
        if (apiKey.isBlank()) return null
        return try {
            val prompt = "A charming, colorful, friendly vector illustration depicting the concept: '$word' ($explanation). Cheerful warm storybook style, clean pastel background, strictly no text or letters."
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    imageConfig = ImageConfig(aspectRatio = "1:1", imageSize = "1K"),
                    responseModalities = listOf("TEXT", "IMAGE")
                )
            )
            val response = apiService.generateImage(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.find { it.inlineData != null }?.inlineData?.data
        } catch (e: Exception) {
            null
        }
    }

    private fun createFallbackIllustration(emoji: String, word: String): String {
        val size = 360
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        // Warm playful background
        val bgPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFF7ED")
            isAntiAlias = true
        }
        canvas.drawRoundRect(0f, 0f, size.toFloat(), size.toFloat(), 48f, 48f, bgPaint)

        // Cheerful inner circle
        val circlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFEDD5")
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size * 0.38f, circlePaint)

        // Accent ring
        val ringPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FF9F1C")
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size * 0.38f, ringPaint)

        // Draw emoji
        val textPaint = android.graphics.Paint().apply {
            textSize = 110f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(emoji, size / 2f, size / 2f + 40f, textPaint)

        return bitmap.toBase64()
    }

    private fun cleanJsonString(input: String): String {
        var trimmed = input.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json")
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```")
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```")
        }
        return trimmed.trim()
    }

    private fun parseOcrJson(jsonStr: String, userNativeLang: String, defaultText: String = ""): OcrScanResult {
        return try {
            val root = JSONObject(jsonStr)
            val title = root.optString("title", "Gescannter Text")
            val langCode = root.optString("detectedLanguageCode", "es")
            val langName = root.optString("detectedLanguageName", "Spanisch")
            val extractedText = root.optString("extractedText", defaultText)

            val diffWordsList = mutableListOf<DifficultWordSuggestion>()
            val diffArray = root.optJSONArray("difficultWords")
            if (diffArray != null) {
                for (i in 0 until diffArray.length()) {
                    val obj = diffArray.getJSONObject(i)
                    diffWordsList.add(
                        DifficultWordSuggestion(
                            word = obj.optString("word", ""),
                            lemma = obj.optString("lemma", ""),
                            partOfSpeech = obj.optString("partOfSpeech", "Wort"),
                            translation = obj.optString("translation", ""),
                            visualEmoji = obj.optString("visualEmoji", "✨"),
                            visualExplanation = obj.optString("visualExplanation", ""),
                            contextSentence = obj.optString("contextSentence", "")
                        )
                    )
                }
            }

            OcrScanResult(
                detectedLanguageCode = langCode,
                detectedLanguageName = langName,
                title = title,
                extractedText = if (extractedText.isNotBlank()) extractedText else defaultText,
                difficultWords = diffWordsList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            getMockOcrResult(userNativeLang)
        }
    }

    private fun getMockOcrResult(userNativeLang: String): OcrScanResult {
        return OcrScanResult(
            detectedLanguageCode = "es",
            detectedLanguageName = "Spanisch",
            title = "Un viaje por los Pirineos",
            extractedText = """El sendero serpenteaba a través de un bosque denso y misterioso. Los árboles centenarios proyectaban sombras alargadas sobre la alfombra de hojas secas. A lo lejos, se vislumbraba una cordillera nevada que brillaba bajo el sol matutino. Cada paso despertaba el susurro de la naturaleza, recordándonos la inmensidad del paisaje y la tranquilidad que solo las alturas pueden otorgar.""",
            difficultWords = listOf(
                DifficultWordSuggestion(
                    word = "serpenteaba",
                    lemma = "serpentear",
                    partOfSpeech = "Verb",
                    translation = if (userNativeLang == "Englisch") "meandered / wound" else "schlängelte sich",
                    visualEmoji = "〰️",
                    visualExplanation = "Ein gewundener Weg, der sich wie eine Schlange in sanften S-Kurven durch das Gelände windet.",
                    contextSentence = "El sendero serpenteaba a través de un bosque denso..."
                ),
                DifficultWordSuggestion(
                    word = "vislumbraba",
                    lemma = "vislumbrar",
                    partOfSpeech = "Verb",
                    translation = if (userNativeLang == "Englisch") "glimpsed / discerned" else "schimmerte durch / erblickte man schemenhaft",
                    visualEmoji = "👁️",
                    visualExplanation = "Etwas vage in der Ferne durch den Morgendunst erkennen, bevor es ganz klar wird.",
                    contextSentence = "A lo lejos, se vislumbraba una cordillera nevada..."
                ),
                DifficultWordSuggestion(
                    word = "cordillera",
                    lemma = "cordillera",
                    partOfSpeech = "Substantiv",
                    translation = if (userNativeLang == "Englisch") "mountain range" else "Gebirgszug / Bergkette",
                    visualEmoji = "🏔️",
                    visualExplanation = "Eine lange Kette von spitzen Berggipfeln nebeneinander, mit Schnee bedeckt am Horizont.",
                    contextSentence = "...se vislumbraba una cordillera nevada..."
                ),
                DifficultWordSuggestion(
                    word = "susurro",
                    lemma = "susurro",
                    partOfSpeech = "Substantiv",
                    translation = if (userNativeLang == "Englisch") "whisper / rustle" else "Flüstern / Rascheln",
                    visualEmoji = "🍃",
                    visualExplanation = "Sanft raschelnde Blätter im Wind, leise wie ein Geheimnis im Ohr.",
                    contextSentence = "Cada paso despertaba el susurro de la naturaleza..."
                ),
                DifficultWordSuggestion(
                    word = "inmensidad",
                    lemma = "inmensidad",
                    partOfSpeech = "Substantiv",
                    translation = if (userNativeLang == "Englisch") "immensity / vastness" else "Unermesslichkeit / Weite",
                    visualEmoji = "🌌",
                    visualExplanation = "Ein riesiger weiter Horizont, der bis ins Unendliche reicht ohne Mauern.",
                    contextSentence = "...recordándonos la inmensidad del paisaje..."
                )
            )
        )
    }

    private fun getMockTextAnalysis(text: String, userNativeLang: String): OcrScanResult {
        // Auto-detect roughly based on sample words
        val lower = text.lowercase()
        val (code, name) = when {
            lower.contains("el ") || lower.contains(" la ") || lower.contains("que ") || lower.contains(" los ") -> "es" to "Spanisch"
            lower.contains("le ") || lower.contains(" la ") || lower.contains("les ") || lower.contains(" une ") -> "fr" to "Französisch"
            lower.contains("the ") || lower.contains(" is ") || lower.contains(" and ") || lower.contains(" of ") -> "en" to "Englisch"
            lower.contains("il ") || lower.contains(" del ") || lower.contains(" che ") || lower.contains(" un ") -> "it" to "Italienisch"
            else -> "de" to "Deutsch"
        }

        val words = text.split(Regex("[\\s.,;:!?()\"„“]+")).filter { it.length > 5 }.distinct()
        val sampleDifficult = words.take(4).map { w ->
            DifficultWordSuggestion(
                word = w,
                lemma = w.lowercase(),
                partOfSpeech = "Wort",
                translation = "Bedeutung von '$w'",
                visualEmoji = "🔍",
                visualExplanation = "Visuelles Symbol für '$w': Einprägsame Szene im Kontext des gelesenen Absatzes.",
                contextSentence = text.take(60) + "..."
            )
        }

        return OcrScanResult(
            detectedLanguageCode = code,
            detectedLanguageName = name,
            title = text.lines().firstOrNull()?.take(30) ?: "Textanalyse",
            extractedText = text,
            difficultWords = sampleDifficult
        )
    }

    private fun getMockWordExplanation(word: String, context: String, userNativeLang: String): WordAnalysisResult {
        return WordAnalysisResult(
            word = word,
            lemma = word.lowercase(),
            partOfSpeech = "Vokabel",
            visualEmoji = "💡",
            visualExplanation = "Stell dir bildhaft vor: Ein zentrales Element, das in '$context' eine tragende Rolle spielt.",
            translation = "Übersetzung für '$word' ($userNativeLang)",
            exampleSentence = context,
            generatedImageBase64 = createFallbackIllustration("💡", word)
        )
    }

    private fun Bitmap.toBase64(): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 85, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
