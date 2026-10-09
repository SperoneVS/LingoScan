package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DocumentEntity
import com.example.data.local.LanguageOption
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.UserPreferences
import com.example.data.local.WordEntity
import com.example.data.remote.DifficultWordSuggestion
import com.example.data.remote.AuthManager
import com.example.data.remote.FirestoreRepository
import com.example.data.remote.GeminiRepository
import com.example.data.remote.OcrScanResult
import com.example.data.remote.WordAnalysisResult
import com.example.ui.model.AvatarCategory
import com.example.ui.model.AvatarCustomization
import com.example.ui.model.QuizMode
import com.example.ui.model.QuizQuestion
import com.example.ui.model.QuizSessionResult
import com.example.ui.model.SampleTextItem
import com.example.ui.model.SuitId
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class AppScreen {
    SCANNER,
    READER,
    VOCABULARY,
    STATISTICS,
    AVATAR_CUSTOMIZER,
    QUIZ,
    QUIZ_SUMMARY
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val wordDao = database.wordDao()
    private val docDao = database.documentDao()
    private val historyDao = database.quizHistoryDao()
    private val preferences = UserPreferences(application)
    private val geminiRepo = GeminiRepository()
    private val authManager = AuthManager(application)
    private val firestoreRepo = FirestoreRepository(application)

    // Auth State
    val currentUser: StateFlow<FirebaseUser?> = authManager.currentUser
    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    // Avatar & Username State
    private val _avatarCustomization = MutableStateFlow(preferences.getAvatarCustomization())
    val avatarCustomization: StateFlow<AvatarCustomization> = _avatarCustomization.asStateFlow()

    // Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.SCANNER)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Native Language
    private val _nativeLanguage = MutableStateFlow(preferences.getSelectedLanguage())
    val nativeLanguage: StateFlow<LanguageOption> = _nativeLanguage.asStateFlow()

    // Database Flows
    val allWords: StateFlow<List<WordEntity>> = wordDao.getAllWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<DocumentEntity>> = docDao.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<QuizHistoryEntity>> = historyDao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStars: StateFlow<Int> = historyDao.getTotalStars()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPoints: StateFlow<Int> = historyDao.getTotalPoints()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Gamification & Streak state for active quiz
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _maxStreak = MutableStateFlow(0)
    val maxStreak: StateFlow<Int> = _maxStreak.asStateFlow()

    private val _sessionPoints = MutableStateFlow(0)
    val sessionPoints: StateFlow<Int> = _sessionPoints.asStateFlow()

    private val _sessionStars = MutableStateFlow(0)
    val sessionStars: StateFlow<Int> = _sessionStars.asStateFlow()

    // Active Reader State
    private val _currentDocument = MutableStateFlow<DocumentEntity?>(null)
    val currentDocument: StateFlow<DocumentEntity?> = _currentDocument.asStateFlow()

    private val _difficultSuggestions = MutableStateFlow<List<DifficultWordSuggestion>>(emptyList())
    val difficultSuggestions: StateFlow<List<DifficultWordSuggestion>> = _difficultSuggestions.asStateFlow()

    private val _markedWordsInDoc = MutableStateFlow<Set<String>>(emptySet())
    val markedWordsInDoc: StateFlow<Set<String>> = _markedWordsInDoc.asStateFlow()

    // Word Inspector Bottom Sheet / Modal
    private val _selectedWordAnalysis = MutableStateFlow<WordAnalysisResult?>(null)
    val selectedWordAnalysis: StateFlow<WordAnalysisResult?> = _selectedWordAnalysis.asStateFlow()

    private val _isAnalyzingWord = MutableStateFlow(false)
    val isAnalyzingWord: StateFlow<Boolean> = _isAnalyzingWord.asStateFlow()

    private val _showTranslation = MutableStateFlow(false)
    val showTranslation: StateFlow<Boolean> = _showTranslation.asStateFlow()

    // OCR / Scan state
    private val _isOcrLoading = MutableStateFlow(false)
    val isOcrLoading: StateFlow<Boolean> = _isOcrLoading.asStateFlow()

    private val _ocrErrorMessage = MutableStateFlow<String?>(null)
    val ocrErrorMessage: StateFlow<String?> = _ocrErrorMessage.asStateFlow()

    // Quiz State
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _quizMode = MutableStateFlow(QuizMode.MULTIPLE_CHOICE)
    val quizMode: StateFlow<QuizMode> = _quizMode.asStateFlow()

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption.asStateFlow()

    private val _typedAnswer = MutableStateFlow("")
    val typedAnswer: StateFlow<String> = _typedAnswer.asStateFlow()

    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow(false)
    val isAnswerCorrect: StateFlow<Boolean> = _isAnswerCorrect.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizResult = MutableStateFlow<QuizSessionResult?>(null)
    val quizResult: StateFlow<QuizSessionResult?> = _quizResult.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setNativeLanguage(option: LanguageOption) {
        preferences.setLanguage(option)
        _nativeLanguage.value = option
        if (currentUser.value != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.saveUserProfile(option.code, option.name)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _isSigningIn.value = true
            try {
                val res = authManager.signInWithGoogle()
                res.onSuccess { user ->
                    firestoreRepo.saveUserProfile(
                        _nativeLanguage.value.code,
                        _nativeLanguage.value.name
                    )
                    syncHistoricWords()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSigningIn.value = false
            }
        }
    }

    fun signOut() {
        authManager.signOut()
    }

    fun syncHistoricWords() {
        viewModelScope.launch {
            try {
                val historicWords = firestoreRepo.fetchHistoricWordsFromCloud()
                historicWords.forEach { w ->
                    wordDao.insertWord(w)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectDocument(doc: DocumentEntity) {
        _currentDocument.value = doc
        parseDifficultSuggestions(doc.difficultWordsJson)
        refreshMarkedWordsForDoc(doc.id)
        _currentScreen.value = AppScreen.READER
    }

    private fun refreshMarkedWordsForDoc(docId: Long) {
        viewModelScope.launch {
            val words = allWords.value.filter { it.documentId == docId }
            _markedWordsInDoc.value = words.map { it.word.lowercase() }.toSet()
        }
    }

    fun scanImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isOcrLoading.value = true
            _ocrErrorMessage.value = null
            try {
                val result = geminiRepo.performOcrAndAnalysis(
                    bitmap = bitmap,
                    userNativeLang = _nativeLanguage.value.name
                )
                result.onSuccess { ocr ->
                    saveAndOpenOcrResult(ocr)
                }.onFailure { err ->
                    _ocrErrorMessage.value = err.message ?: "OCR Fehler"
                }
            } catch (e: Exception) {
                _ocrErrorMessage.value = e.message ?: "Unerwarteter Fehler"
            } finally {
                _isOcrLoading.value = false
            }
        }
    }

    fun scanText(rawText: String, customTitle: String? = null) {
        if (rawText.isBlank()) return
        viewModelScope.launch {
            _isOcrLoading.value = true
            _ocrErrorMessage.value = null
            try {
                val result = geminiRepo.analyzeTextDirectly(
                    rawText = rawText,
                    userNativeLang = _nativeLanguage.value.name
                )
                result.onSuccess { ocr ->
                    val finalOcr = if (!customTitle.isNullOrBlank()) ocr.copy(title = customTitle) else ocr
                    saveAndOpenOcrResult(finalOcr)
                }.onFailure { err ->
                    _ocrErrorMessage.value = err.message ?: "Analysefehler"
                }
            } catch (e: Exception) {
                _ocrErrorMessage.value = e.message ?: "Unerwarteter Fehler"
            } finally {
                _isOcrLoading.value = false
            }
        }
    }

    fun loadSample(sample: SampleTextItem) {
        scanText(rawText = sample.text, customTitle = sample.title)
    }

    private suspend fun saveAndOpenOcrResult(ocr: OcrScanResult) {
        val diffJson = JSONArray().apply {
            ocr.difficultWords.forEach { w ->
                put(JSONObject().apply {
                    put("word", w.word)
                    put("lemma", w.lemma)
                    put("partOfSpeech", w.partOfSpeech)
                    put("translation", w.translation)
                    put("visualEmoji", w.visualEmoji)
                    put("visualExplanation", w.visualExplanation)
                    put("contextSentence", w.contextSentence)
                })
            }
        }.toString()

        val doc = DocumentEntity(
            title = ocr.title,
            rawText = ocr.extractedText,
            detectedLanguageCode = ocr.detectedLanguageCode,
            detectedLanguageName = ocr.detectedLanguageName,
            difficultWordsJson = diffJson
        )
        val docId = docDao.insertDocument(doc)
        val savedDoc = doc.copy(id = docId)
        if (currentUser.value != null) {
            try {
                firestoreRepo.saveDocumentToCloud(savedDoc)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _currentDocument.value = savedDoc
        _difficultSuggestions.value = ocr.difficultWords
        _markedWordsInDoc.value = emptySet()
        _currentScreen.value = AppScreen.READER
    }

    private fun parseDifficultSuggestions(jsonStr: String) {
        val list = mutableListOf<DifficultWordSuggestion>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    DifficultWordSuggestion(
                        word = obj.optString("word"),
                        lemma = obj.optString("lemma"),
                        partOfSpeech = obj.optString("partOfSpeech"),
                        translation = obj.optString("translation"),
                        visualEmoji = obj.optString("visualEmoji", "💡"),
                        visualExplanation = obj.optString("visualExplanation"),
                        contextSentence = obj.optString("contextSentence")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _difficultSuggestions.value = list
    }

    fun onWordTapped(tappedWord: String, sentence: String) {
        val cleanWord = tappedWord.trim().replace(Regex("[^\\p{L}\\p{Nd}-]"), "")
        if (cleanWord.isBlank()) return

        // Check if it matches an already analyzed difficult word
        val existingDiff = _difficultSuggestions.value.find { it.word.equals(cleanWord, ignoreCase = true) }
        val doc = _currentDocument.value
        val sourceLang = doc?.detectedLanguageName ?: "Unbekannt"

        _showTranslation.value = false
        _isAnalyzingWord.value = true

        if (existingDiff != null) {
            _selectedWordAnalysis.value = WordAnalysisResult(
                word = existingDiff.word,
                lemma = existingDiff.lemma.ifBlank { existingDiff.word },
                partOfSpeech = existingDiff.partOfSpeech,
                visualEmoji = existingDiff.visualEmoji,
                visualExplanation = existingDiff.visualExplanation,
                translation = existingDiff.translation,
                exampleSentence = existingDiff.contextSentence.ifBlank { sentence }
            )
            _isAnalyzingWord.value = false
            return
        }

        viewModelScope.launch {
            val res = geminiRepo.explainWord(
                word = cleanWord,
                contextSentence = sentence,
                sourceLang = sourceLang,
                userNativeLang = _nativeLanguage.value.name
            )
            res.onSuccess { analysis ->
                _selectedWordAnalysis.value = analysis
            }.onFailure {
                _selectedWordAnalysis.value = WordAnalysisResult(
                    word = cleanWord,
                    lemma = cleanWord,
                    partOfSpeech = "Wort",
                    visualEmoji = "📖",
                    visualExplanation = "Bedeutung im Kontext von: '$cleanWord'",
                    translation = "Übersetzung...",
                    exampleSentence = sentence
                )
            }
            _isAnalyzingWord.value = false
        }
    }

    fun toggleShowTranslation() {
        _showTranslation.value = !_showTranslation.value
    }

    fun addSelectedWordToVocabulary() {
        val analysis = _selectedWordAnalysis.value ?: return
        val doc = _currentDocument.value

        viewModelScope.launch {
            val entity = WordEntity(
                word = analysis.word,
                lemma = analysis.lemma,
                sourceLanguage = doc?.detectedLanguageName ?: "Unbekannt",
                targetLanguage = _nativeLanguage.value.name,
                translation = analysis.translation,
                visualEmoji = analysis.visualEmoji,
                visualExplanation = analysis.visualExplanation,
                contextSentence = analysis.exampleSentence,
                partOfSpeech = analysis.partOfSpeech,
                isDifficultAutoSuggested = false,
                documentId = doc?.id
            )
            val id = wordDao.insertWord(entity)
            _markedWordsInDoc.value = _markedWordsInDoc.value + analysis.word.lowercase()
            if (currentUser.value != null) {
                try {
                    firestoreRepo.saveWordToCloud(entity.copy(id = id))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addDifficultWordToVocabulary(suggestion: DifficultWordSuggestion) {
        val doc = _currentDocument.value
        viewModelScope.launch {
            val entity = WordEntity(
                word = suggestion.word,
                lemma = suggestion.lemma.ifBlank { suggestion.word },
                sourceLanguage = doc?.detectedLanguageName ?: "Unbekannt",
                targetLanguage = _nativeLanguage.value.name,
                translation = suggestion.translation,
                visualEmoji = suggestion.visualEmoji,
                visualExplanation = suggestion.visualExplanation,
                contextSentence = suggestion.contextSentence,
                partOfSpeech = suggestion.partOfSpeech,
                isDifficultAutoSuggested = true,
                documentId = doc?.id
            )
            val id = wordDao.insertWord(entity)
            _markedWordsInDoc.value = _markedWordsInDoc.value + suggestion.word.lowercase()
            if (currentUser.value != null) {
                try {
                    firestoreRepo.saveWordToCloud(entity.copy(id = id))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addAllDifficultWordsToVocabulary() {
        val doc = _currentDocument.value
        viewModelScope.launch {
            _difficultSuggestions.value.forEach { suggestion ->
                val entity = WordEntity(
                    word = suggestion.word,
                    lemma = suggestion.lemma.ifBlank { suggestion.word },
                    sourceLanguage = doc?.detectedLanguageName ?: "Unbekannt",
                    targetLanguage = _nativeLanguage.value.name,
                    translation = suggestion.translation,
                    visualEmoji = suggestion.visualEmoji,
                    visualExplanation = suggestion.visualExplanation,
                    contextSentence = suggestion.contextSentence,
                    partOfSpeech = suggestion.partOfSpeech,
                    isDifficultAutoSuggested = true,
                    documentId = doc?.id
                )
                val id = wordDao.insertWord(entity)
                if (currentUser.value != null) {
                    try {
                        firestoreRepo.saveWordToCloud(entity.copy(id = id))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            _markedWordsInDoc.value = _markedWordsInDoc.value + _difficultSuggestions.value.map { it.word.lowercase() }
        }
    }

    fun dismissWordAnalysis() {
        _selectedWordAnalysis.value = null
        _showTranslation.value = false
    }

    fun deleteWord(word: WordEntity) {
        viewModelScope.launch {
            wordDao.deleteWord(word)
            _markedWordsInDoc.value = _markedWordsInDoc.value - word.word.lowercase()
            if (currentUser.value != null) {
                try {
                    firestoreRepo.deleteWordFromCloud(word.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Vocabulary Quiz logic
    fun startQuizFromCurrentDoc(mode: QuizMode) {
        viewModelScope.launch {
            val docId = _currentDocument.value?.id
            var docWords = allWords.value.filter { it.documentId == docId }

            // If user marked 0 or few words (< 2), auto-include the difficult suggestions!
            if (docWords.size < 2 && _difficultSuggestions.value.isNotEmpty()) {
                val addedWords = mutableListOf<WordEntity>()
                _difficultSuggestions.value.forEach { suggestion ->
                    val entity = WordEntity(
                        word = suggestion.word,
                        lemma = suggestion.lemma.ifBlank { suggestion.word },
                        sourceLanguage = _currentDocument.value?.detectedLanguageName ?: "Unbekannt",
                        targetLanguage = _nativeLanguage.value.name,
                        translation = suggestion.translation,
                        visualEmoji = suggestion.visualEmoji,
                        visualExplanation = suggestion.visualExplanation,
                        contextSentence = suggestion.contextSentence,
                        partOfSpeech = suggestion.partOfSpeech,
                        isDifficultAutoSuggested = true,
                        documentId = docId
                    )
                    wordDao.insertWord(entity)
                    addedWords.add(entity)
                }
                docWords = docWords + addedWords
            }

            if (docWords.isEmpty()) {
                // If still empty, use any available words or mock words
                docWords = allWords.value
            }

            setupQuizSession(docWords, mode)
        }
    }

    fun startQuizFromAllWords(mode: QuizMode) {
        setupQuizSession(allWords.value, mode)
    }

    private fun setupQuizSession(sourceWords: List<WordEntity>, mode: QuizMode) {
        if (sourceWords.isEmpty()) return

        val shuffled = sourceWords.shuffled().take(10)
        val allTranslationsPool = allWords.value.map { it.translation }.distinct()

        val questions = shuffled.map { wordItem ->
            val options = if (mode == QuizMode.MULTIPLE_CHOICE) {
                generateMultipleChoiceOptions(wordItem.translation, allTranslationsPool)
            } else {
                emptyList()
            }
            QuizQuestion(
                wordEntity = wordItem,
                mode = mode,
                options = options,
                correctTranslation = wordItem.translation,
                sourceContextSentence = wordItem.contextSentence
            )
        }

        _quizMode.value = mode
        _quizQuestions.value = questions
        _currentQuizIndex.value = 0
        _quizScore.value = 0
        _currentStreak.value = 0
        _maxStreak.value = 0
        _sessionPoints.value = 0
        _sessionStars.value = 0
        _selectedOption.value = null
        _typedAnswer.value = ""
        _isAnswerChecked.value = false
        _isAnswerCorrect.value = false
        _quizResult.value = null
        _currentScreen.value = AppScreen.QUIZ
    }

    private fun generateMultipleChoiceOptions(correct: String, pool: List<String>): List<String> {
        val distractors = pool.filter { it != correct }.shuffled().take(3).toMutableList()
        val defaultDistractors = listOf(
            "etwas verbergen", "schnell voranschreiten", "stille Bewunderung",
            "plötzliches Geräusch", "weites Tal", "geheimer Pfad", "kristallklares Wasser"
        )
        while (distractors.size < 3) {
            val candidate = defaultDistractors.filter { it != correct && !distractors.contains(it) }.randomOrNull()
                ?: "weitere Bedeutung ${distractors.size + 1}"
            distractors.add(candidate)
        }
        return (distractors + correct).shuffled()
    }

    fun selectQuizOption(option: String) {
        if (_isAnswerChecked.value) return
        _selectedOption.value = option
    }

    fun updateTypedAnswer(text: String) {
        if (_isAnswerChecked.value) return
        _typedAnswer.value = text
    }

    fun submitAnswer() {
        if (_isAnswerChecked.value) return
        val currentQ = _quizQuestions.value.getOrNull(_currentQuizIndex.value) ?: return

        val isCorrect: Boolean
        if (currentQ.mode == QuizMode.MULTIPLE_CHOICE) {
            val chosen = _selectedOption.value ?: return
            isCorrect = chosen.trim().equals(currentQ.correctTranslation.trim(), ignoreCase = true)
        } else {
            val typed = _typedAnswer.value.trim().lowercase()
            val target = currentQ.correctTranslation.trim().lowercase()
            // Lenient matching: matches or contains key segment
            val cleanTarget = target.replace(Regex("[/;,]"), " ")
            isCorrect = typed.isNotEmpty() && (
                typed == target ||
                cleanTarget.split(" ").any { it.length > 2 && typed.contains(it) } ||
                target.split("/").any { it.trim() == typed }
            )
        }

        _isAnswerChecked.value = true
        _isAnswerCorrect.value = isCorrect

        viewModelScope.launch {
            if (isCorrect) {
                val newStreak = _currentStreak.value + 1
                _currentStreak.value = newStreak
                if (newStreak > _maxStreak.value) {
                    _maxStreak.value = newStreak
                }

                val basePoints = if (currentQ.mode == QuizMode.TEXT_INPUT) 15 else 10
                val streakBonus = if (newStreak >= 3) 5 else 0
                val earnedPoints = basePoints + streakBonus
                val earnedStars = if (currentQ.mode == QuizMode.TEXT_INPUT) 2 else 1

                _sessionPoints.value = _sessionPoints.value + earnedPoints
                _sessionStars.value = _sessionStars.value + earnedStars
                _quizScore.value = _quizScore.value + 1
                wordDao.recordCorrectAnswer(currentQ.wordEntity.id)
            } else {
                _currentStreak.value = 0
                wordDao.recordWrongAnswer(currentQ.wordEntity.id)
            }
        }
    }

    fun nextQuizQuestion() {
        val nextIdx = _currentQuizIndex.value + 1
        if (nextIdx < _quizQuestions.value.size) {
            _currentQuizIndex.value = nextIdx
            _selectedOption.value = null
            _typedAnswer.value = ""
            _isAnswerChecked.value = false
            _isAnswerCorrect.value = false
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val total = _quizQuestions.value.size
        val correct = _quizScore.value
        val accuracy = if (total > 0) (correct * 100) / total else 0
        val points = _sessionPoints.value
        val stars = _sessionStars.value
        val maxStr = _maxStreak.value
        val docTitle = _currentDocument.value?.title ?: "Vokabeltraining"

        val historyEntity = QuizHistoryEntity(
            timestamp = System.currentTimeMillis(),
            mode = _quizMode.value.name,
            totalQuestions = total,
            correctCount = correct,
            wrongCount = total - correct,
            pointsEarned = points,
            starsEarned = stars,
            accuracyPercentage = accuracy,
            sourceTitle = docTitle
        )

        viewModelScope.launch {
            historyDao.insertHistory(historyEntity)
            if (currentUser.value != null) {
                try {
                    firestoreRepo.saveQuizHistoryToCloud(historyEntity)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val result = QuizSessionResult(
            totalQuestions = total,
            correctCount = correct,
            wrongCount = total - correct,
            learnedWords = _quizQuestions.value.map { it.wordEntity },
            mode = _quizMode.value,
            pointsEarned = points,
            starsEarned = stars,
            maxStreak = maxStr,
            sourceTitle = docTitle
        )
        _quizResult.value = result
        _currentScreen.value = AppScreen.QUIZ_SUMMARY
    }

    fun restartQuiz(mode: QuizMode) {
        val words = _quizResult.value?.learnedWords ?: allWords.value
        setupQuizSession(words, mode)
    }

    // Avatar and Username methods
    fun updateAvatar(customization: AvatarCustomization) {
        _avatarCustomization.value = customization
        preferences.saveAvatarCustomization(customization)
        if (currentUser.value != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.saveAvatarToCloud(customization)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateUsername(newUsername: String) {
        val trimmed = newUsername.trim()
        val nameToSave = if (trimmed.isBlank()) "LernPionier" else trimmed
        val updated = _avatarCustomization.value.copy(username = nameToSave)
        updateAvatar(updated)
    }

    fun selectSuit(suitId: SuitId) {
        val unlocked = _avatarCustomization.value.unlockedSuits
        if (suitId.id in unlocked) {
            val updated = _avatarCustomization.value.copy(suitId = suitId)
            updateAvatar(updated)
        }
    }

    fun unlockSuit(suitId: SuitId): Boolean {
        val stars = totalStars.value
        if (stars >= suitId.starCost) {
            val newUnlocked = _avatarCustomization.value.unlockedSuits + suitId.id
            val updated = _avatarCustomization.value.copy(
                suitId = suitId,
                unlockedSuits = newUnlocked
            )
            updateAvatar(updated)
            return true
        }
        return false
    }

    fun updateAvatarCategoryOption(category: AvatarCategory, index: Int) {
        val current = _avatarCustomization.value
        val updated = when (category) {
            AvatarCategory.SKIN_TONE -> current.copy(skinToneIndex = index)
            AvatarCategory.EXPRESSION -> current.copy(expressionIndex = index)
            AvatarCategory.HAIRSTYLE -> current.copy(hairStyleIndex = index)
            AvatarCategory.GLASSES -> current.copy(glassesIndex = index)
            AvatarCategory.FACIAL_HAIR -> current.copy(facialHairIndex = index)
            AvatarCategory.HEADWEAR -> current.copy(headwearIndex = index)
        }
        updateAvatar(updated)
    }

    fun updateAvatarCategoryColor(category: AvatarCategory, colorIndex: Int) {
        val current = _avatarCustomization.value
        val updated = when (category) {
            AvatarCategory.SKIN_TONE -> current.copy(skinToneIndex = colorIndex)
            AvatarCategory.EXPRESSION -> current // Expression uses hair/eye palettes
            AvatarCategory.HAIRSTYLE -> current.copy(hairColorIndex = colorIndex)
            AvatarCategory.GLASSES -> current.copy(glassesColorIndex = colorIndex)
            AvatarCategory.FACIAL_HAIR -> current.copy(facialHairColorIndex = colorIndex)
            AvatarCategory.HEADWEAR -> current.copy(headwearColorIndex = colorIndex)
        }
        updateAvatar(updated)
    }
}

