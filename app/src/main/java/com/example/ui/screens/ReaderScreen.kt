package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DocumentEntity
import com.example.data.remote.DifficultWordSuggestion
import com.example.data.remote.WordAnalysisResult
import com.example.ui.components.WordInspectorSheet
import com.example.ui.model.QuizMode

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    document: DocumentEntity?,
    markedWords: Set<String>,
    difficultSuggestions: List<DifficultWordSuggestion>,
    selectedAnalysis: WordAnalysisResult?,
    isAnalyzing: Boolean,
    showTranslation: Boolean,
    nativeLanguageName: String,
    onWordTapped: (String, String) -> Unit,
    onToggleTranslation: () -> Unit,
    onSaveSelectedWord: () -> Unit,
    onAddDifficultWord: (DifficultWordSuggestion) -> Unit,
    onAddAllDifficultWords: () -> Unit,
    onDismissInspector: () -> Unit,
    onStartQuiz: (QuizMode) -> Unit,
    modifier: Modifier = Modifier
) {
    if (document == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Kein Text ausgewählt. Bitte scanne zuerst ein Dokument.")
        }
        return
    }

    var showQuizModeDialog by remember { mutableStateOf(false) }

    val rawWords = remember(document.rawText) {
        document.rawText.split("\n\n").map { paragraph ->
            paragraph.split(" ").filter { it.isNotBlank() }
        }
    }

    val userMarkedCount = markedWords.size
    val isFewWordsMarked = userMarkedCount < 2 && difficultSuggestions.isNotEmpty()

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title & Language Badge
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🌐 Automatisch erkannt: ${document.detectedLanguageName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$userMarkedCount Wörter markiert",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Tip Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tippe auf ein unbekanntes Wort im Text, um eine visuelle Erklärung zu erhalten.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Interactive Text Paragraphs
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rawWords.forEachIndexed { pIndex, paragraphWords ->
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                paragraphWords.forEachIndexed { wIndex, token ->
                                    val cleanToken = token.trim().replace(Regex("[^\\p{L}\\p{Nd}-]"), "").lowercase()
                                    val isMarked = markedWords.contains(cleanToken)
                                    val isSuggestedDifficult = difficultSuggestions.any { it.word.lowercase() == cleanToken }

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                // Find the surrounding sentence
                                                val sentence = paragraphWords.joinToString(" ")
                                                onWordTapped(token, sentence)
                                            }
                                            .testTag("word_${pIndex}_$wIndex"),
                                        color = when {
                                            isMarked -> MaterialTheme.colorScheme.primaryContainer
                                            isSuggestedDifficult -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                            else -> MaterialTheme.colorScheme.surface
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = token,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 18.sp,
                                            lineHeight = 26.sp,
                                            fontWeight = if (isMarked || isSuggestedDifficult) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isMarked -> MaterialTheme.colorScheme.onPrimaryContainer
                                                isSuggestedDifficult -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SPOTLIGHT BANNER: If few or no words were marked
            if (isFewWordsMarked) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("difficult_words_spotlight_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "Anspruchsvolle Wörter entdeckt!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Button(
                                    onClick = onAddAllDifficultWords,
                                    modifier = Modifier.testTag("add_all_difficult_btn")
                                ) {
                                    Text("Alle lernen", fontSize = 12.sp)
                                }
                            }

                            Text(
                                text = "Du hast bisher wenige Wörter markiert. Die App hat stichprobenartig besonders schwierige Wörter aus diesem Text herausgesucht:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(difficultSuggestions) { suggestion ->
                                    val isAdded = markedWords.contains(suggestion.word.lowercase())
                                    Card(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .clickable {
                                                onWordTapped(suggestion.word, suggestion.contextSentence)
                                            }
                                            .testTag("diff_card_${suggestion.word}"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(text = suggestion.visualEmoji, fontSize = 20.sp)
                                                Surface(
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = suggestion.partOfSpeech,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = suggestion.word,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = suggestion.visualExplanation,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 2,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            OutlinedButton(
                                                onClick = { onAddDifficultWord(suggestion) },
                                                enabled = !isAdded,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(if (isAdded) "Gespeichert ✓" else "+ Lernen", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Action Bar: Vokabeltraining starten
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showQuizModeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_quiz_from_reader_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFewWordsMarked)
                            "Vokabeltraining starten (inkl. anspruchsvolle Wörter)"
                        else
                            "Vokabeltraining mit markierten Wörtern",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Quiz Mode Selection Dialog
    if (showQuizModeDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showQuizModeDialog = false },
            title = {
                Text("Vokabeltraining starten", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Wähle deinen Trainingsmodus für die Vokabeln aus diesem Text:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showQuizModeDialog = false
                                onStartQuiz(QuizMode.MULTIPLE_CHOICE)
                            }
                            .testTag("mode_mc_button"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎯", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Multiple Choice",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Wähle aus 4 Übersetzungen in $nativeLanguageName",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showQuizModeDialog = false
                                onStartQuiz(QuizMode.TEXT_INPUT)
                            }
                            .testTag("mode_input_button"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⌨️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bedeutung selbst eingeben",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tippe die Bedeutung in $nativeLanguageName ab",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Word Inspector Bottom Sheet
    if (selectedAnalysis != null || isAnalyzing) {
        val isSaved = selectedAnalysis != null && markedWords.contains(selectedAnalysis.word.lowercase())
        WordInspectorSheet(
            analysis = selectedAnalysis,
            isLoading = isAnalyzing,
            isSaved = isSaved,
            showTranslation = showTranslation,
            nativeLanguageName = nativeLanguageName,
            onToggleTranslation = onToggleTranslation,
            onSaveToVocabulary = onSaveSelectedWord,
            onDismiss = onDismissInspector
        )
    }
}
