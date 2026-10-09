package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.QuizMode
import com.example.ui.model.QuizQuestion

@Composable
fun QuizScreen(
    question: QuizQuestion?,
    currentIndex: Int,
    totalCount: Int,
    score: Int,
    stars: Int = 0,
    points: Int = 0,
    streak: Int = 0,
    nativeLanguageName: String,
    quizMode: QuizMode,
    selectedOption: String?,
    typedAnswer: String,
    isAnswerChecked: Boolean,
    isAnswerCorrect: Boolean,
    onSelectOption: (String) -> Unit,
    onUpdateTypedAnswer: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onQuitQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (question == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Keine Quizfrage geladen.")
        }
        return
    }

    val progress = (currentIndex + 1).toFloat() / totalCount.coerceAtLeast(1).toFloat()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header: Progress, Stars & Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Vokabeltraining: ${currentIndex + 1}/$totalCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (streak >= 2) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔥 $streak Serie!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (quizMode == QuizMode.MULTIPLE_CHOICE)
                            "Multiple Choice 🎯"
                        else
                            "Bedeutung eintippen ✍️",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Stars Chip
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⭐ $stars",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }

                // Points Chip
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🏆 $points",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }

                IconButton(onClick = onQuitQuiz, modifier = Modifier.testTag("quit_quiz_button")) {
                    Icon(Icons.Default.Close, contentDescription = "Quiz beenden")
                }
            }
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        // Question Card
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = question.wordEntity.visualEmoji, fontSize = 32.sp)
                }

                Text(
                    text = question.wordEntity.word,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${question.wordEntity.partOfSpeech} • ${question.wordEntity.sourceLanguage}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (question.sourceContextSentence.isNotBlank()) {
                    Text(
                        text = "„${question.sourceContextSentence}“",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // MODE 1: MULTIPLE CHOICE
        if (quizMode == QuizMode.MULTIPLE_CHOICE) {
            Text(
                text = "Was bedeutet dieses Wort auf $nativeLanguageName?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    val isCorrectOption = option.trim().equals(question.correctTranslation.trim(), ignoreCase = true)

                    val containerColor by animateColorAsState(
                        targetValue = when {
                            isAnswerChecked && isCorrectOption -> Color(0xFF10B981).copy(alpha = 0.2f)
                            isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        },
                        label = "mc_color"
                    )

                    val borderColor = when {
                        isAnswerChecked && isCorrectOption -> Color(0xFF10B981)
                        isAnswerChecked && isSelected && !isAnswerCorrect -> Color(0xFFEF4444)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> Color.Transparent
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswerChecked) { onSelectOption(option) }
                            .testTag("option_$index"),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${('A'.code + index).toChar()}.",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected || (isAnswerChecked && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (isAnswerChecked) {
                                if (isCorrectOption) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Richtig", tint = Color(0xFF10B981))
                                } else if (isSelected) {
                                    Icon(Icons.Default.Error, contentDescription = "Falsch", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // MODE 2: TEXT INPUT
            Text(
                text = "Gib die Bedeutung auf $nativeLanguageName ein:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = typedAnswer,
                onValueChange = onUpdateTypedAnswer,
                placeholder = { Text("Bedeutung eintippen...") },
                enabled = !isAnswerChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_text_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // FEEDBACK BANNER AFTER CHECKING
        AnimatedVisibility(visible = isAnswerChecked) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isAnswerCorrect)
                        Color(0xFF10B981).copy(alpha = 0.15f)
                    else
                        Color(0xFFEF4444).copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("quiz_feedback_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isAnswerCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        Text(
                            text = if (isAnswerCorrect) "Bähm! Volltreffer! 🎉🧠" else "Hoppla, fast! Aus Fehlern wird man schlau! 🎈",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAnswerCorrect) Color(0xFF065F46) else Color(0xFF991B1B)
                        )
                    }

                    Text(
                        text = "Richtige Bedeutung: ${question.correctTranslation}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Visuelle Merkhilfe: ${question.wordEntity.visualExplanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ACTION BUTTON: Check answer OR Next question
        if (!isAnswerChecked) {
            val canSubmit = if (quizMode == QuizMode.MULTIPLE_CHOICE)
                selectedOption != null
            else
                typedAnswer.isNotBlank()

            Button(
                onClick = onSubmitAnswer,
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_quiz_answer_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Antwort prüfen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("next_quiz_question_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (currentIndex + 1 < totalCount) "Nächste Frage" else "Ergebnis anzeigen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}
