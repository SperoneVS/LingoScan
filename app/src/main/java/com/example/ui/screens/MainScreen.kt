package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AccountDialog
import com.example.ui.components.AvatarView
import com.example.ui.components.LanguageSettingsDialog
import com.example.ui.model.QuizMode
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val nativeLanguage by viewModel.nativeLanguage.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()
    val allDocuments by viewModel.allDocuments.collectAsStateWithLifecycle()
    val currentDocument by viewModel.currentDocument.collectAsStateWithLifecycle()
    val difficultSuggestions by viewModel.difficultSuggestions.collectAsStateWithLifecycle()
    val markedWordsInDoc by viewModel.markedWordsInDoc.collectAsStateWithLifecycle()
    val selectedAnalysis by viewModel.selectedWordAnalysis.collectAsStateWithLifecycle()
    val isAnalyzingWord by viewModel.isAnalyzingWord.collectAsStateWithLifecycle()
    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()
    val isOcrLoading by viewModel.isOcrLoading.collectAsStateWithLifecycle()

    // Gamification & Avatar states
    val totalStars by viewModel.totalStars.collectAsStateWithLifecycle()
    val totalPoints by viewModel.totalPoints.collectAsStateWithLifecycle()
    val quizHistory by viewModel.quizHistory.collectAsStateWithLifecycle()
    val avatarCustomization by viewModel.avatarCustomization.collectAsStateWithLifecycle()

    // Quiz states
    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val currentQuizIndex by viewModel.currentQuizIndex.collectAsStateWithLifecycle()
    val quizMode by viewModel.quizMode.collectAsStateWithLifecycle()
    val selectedOption by viewModel.selectedOption.collectAsStateWithLifecycle()
    val typedAnswer by viewModel.typedAnswer.collectAsStateWithLifecycle()
    val isAnswerChecked by viewModel.isAnswerChecked.collectAsStateWithLifecycle()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val quizResult by viewModel.quizResult.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    // Back handling for sub-screens
    BackHandler(enabled = currentScreen != AppScreen.SCANNER) {
        when (currentScreen) {
            AppScreen.READER -> viewModel.navigateTo(AppScreen.SCANNER)
            AppScreen.VOCABULARY -> viewModel.navigateTo(AppScreen.SCANNER)
            AppScreen.STATISTICS -> viewModel.navigateTo(AppScreen.SCANNER)
            AppScreen.AVATAR_CUSTOMIZER -> viewModel.navigateTo(AppScreen.STATISTICS)
            AppScreen.QUIZ -> viewModel.navigateTo(AppScreen.READER)
            AppScreen.QUIZ_SUMMARY -> viewModel.navigateTo(AppScreen.STATISTICS)
            AppScreen.SCANNER -> { /* No back */ }
        }
    }

    if (showLanguageDialog) {
        LanguageSettingsDialog(
            currentLanguage = nativeLanguage,
            onLanguageSelected = { viewModel.setNativeLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showAccountDialog) {
        AccountDialog(
            currentUser = currentUser,
            isSigningIn = isSigningIn,
            onSignInWithGoogle = { viewModel.signInWithGoogle() },
            onSignOut = { viewModel.signOut() },
            onSyncHistoricWords = { viewModel.syncHistoricWords() },
            onDismiss = { showAccountDialog = false }
        )
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.SCANNER,
        AppScreen.READER,
        AppScreen.VOCABULARY,
        AppScreen.STATISTICS
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != AppScreen.AVATAR_CUSTOMIZER) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LingoScan ✨",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        if (currentScreen != AppScreen.SCANNER) {
                            IconButton(
                                onClick = {
                                    when (currentScreen) {
                                        AppScreen.READER -> viewModel.navigateTo(AppScreen.SCANNER)
                                        AppScreen.VOCABULARY -> viewModel.navigateTo(AppScreen.SCANNER)
                                        AppScreen.STATISTICS -> viewModel.navigateTo(AppScreen.SCANNER)
                                        AppScreen.AVATAR_CUSTOMIZER -> viewModel.navigateTo(AppScreen.STATISTICS)
                                        AppScreen.QUIZ -> viewModel.navigateTo(AppScreen.READER)
                                        AppScreen.QUIZ_SUMMARY -> viewModel.navigateTo(AppScreen.STATISTICS)
                                        else -> {}
                                    }
                                },
                                modifier = Modifier.testTag("top_bar_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Zurück"
                                )
                            }
                        }
                    },
                    actions = {
                        // Avatar & Username Pill (Clickable -> Opens Avatar/Skins customizer!)
                        Surface(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.navigateTo(AppScreen.AVATAR_CUSTOMIZER) }
                                .testTag("top_avatar_pill"),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarView(
                                    customization = avatarCustomization,
                                    size = 28.dp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = avatarCustomization.username.take(9),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "⭐$totalStars",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17)
                                )
                            }
                        }

                        // Google Account Sync Button
                        Surface(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showAccountDialog = true }
                                .testTag("top_account_pill"),
                            color = if (currentUser != null)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentUser != null) {
                                    Text(
                                        text = "☁️",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                } else {
                                    Text(
                                        text = "G",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Native Language Selector Pill
                        Surface(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showLanguageDialog = true }
                                .testTag("top_language_pill"),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = nativeLanguage.flagEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = nativeLanguage.code.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SCANNER,
                        onClick = { viewModel.navigateTo(AppScreen.SCANNER) },
                        icon = {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scannen")
                        },
                        label = { Text("Scannen") },
                        modifier = Modifier.testTag("nav_scanner")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.READER,
                        onClick = {
                            if (currentDocument != null) {
                                viewModel.navigateTo(AppScreen.READER)
                            } else if (allDocuments.isNotEmpty()) {
                                viewModel.selectDocument(allDocuments.first())
                            } else {
                                viewModel.navigateTo(AppScreen.SCANNER)
                            }
                        },
                        icon = {
                            Icon(Icons.Default.Book, contentDescription = "Lesen")
                        },
                        label = { Text("Text lesen") },
                        modifier = Modifier.testTag("nav_reader")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.VOCABULARY,
                        onClick = { viewModel.navigateTo(AppScreen.VOCABULARY) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (allWords.isNotEmpty()) {
                                        Badge { Text("${allWords.size}") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.School, contentDescription = "Vokabeln")
                            }
                        },
                        label = { Text("Vokabeln") },
                        modifier = Modifier.testTag("nav_vocabulary")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.STATISTICS,
                        onClick = { viewModel.navigateTo(AppScreen.STATISTICS) },
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Statistik")
                        },
                        label = { Text("Statistik") },
                        modifier = Modifier.testTag("nav_statistics")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            label = "screen_crossfade"
        ) { screen ->
            when (screen) {
                AppScreen.SCANNER -> {
                    ScannerScreen(
                        nativeLanguage = nativeLanguage,
                        isOcrLoading = isOcrLoading,
                        recentDocuments = allDocuments,
                        onScanImage = { bitmap -> viewModel.scanImage(bitmap) },
                        onScanText = { text, title -> viewModel.scanText(text, title) },
                        onLoadSample = { sample -> viewModel.loadSample(sample) },
                        onSelectRecentDoc = { doc -> viewModel.selectDocument(doc) },
                        onOpenLanguagePicker = { showLanguageDialog = true }
                    )
                }
                AppScreen.READER -> {
                    ReaderScreen(
                        document = currentDocument,
                        markedWords = markedWordsInDoc,
                        difficultSuggestions = difficultSuggestions,
                        selectedAnalysis = selectedAnalysis,
                        isAnalyzing = isAnalyzingWord,
                        showTranslation = showTranslation,
                        nativeLanguageName = nativeLanguage.name,
                        onWordTapped = { word, sentence -> viewModel.onWordTapped(word, sentence) },
                        onToggleTranslation = { viewModel.toggleShowTranslation() },
                        onSaveSelectedWord = { viewModel.addSelectedWordToVocabulary() },
                        onAddDifficultWord = { diff -> viewModel.addDifficultWordToVocabulary(diff) },
                        onAddAllDifficultWords = { viewModel.addAllDifficultWordsToVocabulary() },
                        onDismissInspector = { viewModel.dismissWordAnalysis() },
                        onStartQuiz = { mode -> viewModel.startQuizFromCurrentDoc(mode) }
                    )
                }
                AppScreen.VOCABULARY -> {
                    VocabularyScreen(
                        words = allWords,
                        nativeLanguageName = nativeLanguage.name,
                        onDeleteWord = { viewModel.deleteWord(it) },
                        onStartQuiz = { mode -> viewModel.startQuizFromAllWords(mode) }
                    )
                }
                AppScreen.STATISTICS -> {
                    StatisticsScreen(
                        avatarCustomization = avatarCustomization,
                        totalStars = totalStars,
                        totalPoints = totalPoints,
                        quizHistory = quizHistory,
                        allWords = allWords,
                        onOpenAvatarCustomizer = { viewModel.navigateTo(AppScreen.AVATAR_CUSTOMIZER) },
                        onStartQuiz = {
                            viewModel.startQuizFromAllWords(QuizMode.MULTIPLE_CHOICE)
                        }
                    )
                }
                AppScreen.AVATAR_CUSTOMIZER -> {
                    AvatarScreen(
                        customization = avatarCustomization,
                        totalStars = totalStars,
                        onSaveAvatar = { viewModel.updateAvatar(it) },
                        onUpdateUsername = { viewModel.updateUsername(it) },
                        onSelectSuit = { viewModel.selectSuit(it) },
                        onUnlockSuit = { viewModel.unlockSuit(it) },
                        onUpdateCategoryOption = { cat, idx -> viewModel.updateAvatarCategoryOption(cat, idx) },
                        onUpdateCategoryColor = { cat, idx -> viewModel.updateAvatarCategoryColor(cat, idx) },
                        onClose = { viewModel.navigateTo(AppScreen.STATISTICS) }
                    )
                }
                AppScreen.QUIZ -> {
                    val q = quizQuestions.getOrNull(currentQuizIndex)
                    QuizScreen(
                        question = q,
                        currentIndex = currentQuizIndex,
                        totalCount = quizQuestions.size,
                        score = quizScore,
                        nativeLanguageName = nativeLanguage.name,
                        quizMode = quizMode,
                        selectedOption = selectedOption,
                        typedAnswer = typedAnswer,
                        isAnswerChecked = isAnswerChecked,
                        isAnswerCorrect = isAnswerCorrect,
                        onSelectOption = { viewModel.selectQuizOption(it) },
                        onUpdateTypedAnswer = { viewModel.updateTypedAnswer(it) },
                        onSubmitAnswer = { viewModel.submitAnswer() },
                        onNextQuestion = { viewModel.nextQuizQuestion() },
                        onQuitQuiz = { viewModel.navigateTo(AppScreen.READER) }
                    )
                }
                AppScreen.QUIZ_SUMMARY -> {
                    QuizSummaryScreen(
                        result = quizResult,
                        onRestartQuiz = { mode -> viewModel.restartQuiz(mode) },
                        onBackToVocabulary = { viewModel.navigateTo(AppScreen.VOCABULARY) },
                        onBackToScanner = { viewModel.navigateTo(AppScreen.SCANNER) }
                    )
                }
            }
        }
    }
}
