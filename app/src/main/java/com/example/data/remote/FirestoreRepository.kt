package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.data.local.DocumentEntity
import com.example.data.local.QuizHistoryEntity
import com.example.data.local.WordEntity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Secondary constructor resolving named custom database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: error("User must be signed in to perform Firestore operations")
    }

    suspend fun saveUserProfile(nativeLangCode: String, nativeLangName: String) {
        val uid = auth.currentUser?.uid ?: return
        val userDoc = db.collection("users").document(uid)
        val data = hashMapOf(
            "userId" to uid,
            "displayName" to (auth.currentUser?.displayName ?: "Nutzer"),
            "email" to (auth.currentUser?.email ?: ""),
            "nativeLanguageCode" to nativeLangCode,
            "nativeLanguageName" to nativeLangName,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userDoc.set(data, SetOptions.merge()).await()
    }

    suspend fun saveWordToCloud(word: WordEntity): String {
        val uid = requireUserId()
        val docRef = if (word.id > 0) {
            db.collection("users").document(uid).collection("words").document(word.id.toString())
        } else {
            db.collection("users").document(uid).collection("words").document()
        }

        val data = hashMapOf(
            "userId" to uid,
            "word" to word.word,
            "lemma" to word.lemma,
            "sourceLanguage" to word.sourceLanguage,
            "targetLanguage" to word.targetLanguage,
            "translation" to word.translation,
            "visualEmoji" to word.visualEmoji,
            "visualExplanation" to word.visualExplanation,
            "contextSentence" to word.contextSentence,
            "partOfSpeech" to word.partOfSpeech,
            "isDifficultAutoSuggested" to word.isDifficultAutoSuggested,
            "masteryLevel" to word.masteryLevel,
            "correctCount" to word.correctCount,
            "wrongCount" to word.wrongCount,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data, SetOptions.merge()).await()
        return docRef.id
    }

    suspend fun deleteWordFromCloud(wordId: Long) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("words").document(wordId.toString())
            .delete().await()
    }

    suspend fun saveDocumentToCloud(doc: DocumentEntity): String {
        val uid = requireUserId()
        val docRef = if (doc.id > 0) {
            db.collection("users").document(uid).collection("documents").document(doc.id.toString())
        } else {
            db.collection("users").document(uid).collection("documents").document()
        }

        val data = hashMapOf(
            "userId" to uid,
            "title" to doc.title,
            "rawText" to doc.rawText,
            "detectedLanguageCode" to doc.detectedLanguageCode,
            "detectedLanguageName" to doc.detectedLanguageName,
            "difficultWordsJson" to doc.difficultWordsJson,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data, SetOptions.merge()).await()
        return docRef.id
    }

    suspend fun fetchHistoricWordsFromCloud(): List<WordEntity> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("users").document(uid).collection("words").get().await()
        return snapshot.documents.mapNotNull { d ->
            val word = d.getString("word") ?: return@mapNotNull null
            WordEntity(
                id = d.id.toLongOrNull() ?: 0L,
                word = word,
                lemma = d.getString("lemma") ?: word,
                sourceLanguage = d.getString("sourceLanguage") ?: "Unbekannt",
                targetLanguage = d.getString("targetLanguage") ?: "Deutsch",
                translation = d.getString("translation") ?: "",
                visualEmoji = d.getString("visualEmoji") ?: "💡",
                visualExplanation = d.getString("visualExplanation") ?: "",
                contextSentence = d.getString("contextSentence") ?: "",
                partOfSpeech = d.getString("partOfSpeech") ?: "Wort",
                isDifficultAutoSuggested = d.getBoolean("isDifficultAutoSuggested") ?: false,
                masteryLevel = d.getLong("masteryLevel")?.toInt() ?: 0,
                correctCount = d.getLong("correctCount")?.toInt() ?: 0,
                wrongCount = d.getLong("wrongCount")?.toInt() ?: 0
            )
        }
    }

    suspend fun saveQuizHistoryToCloud(item: QuizHistoryEntity): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("quizHistory").document()
        val data = hashMapOf(
            "userId" to uid,
            "mode" to item.mode,
            "totalQuestions" to item.totalQuestions,
            "correctCount" to item.correctCount,
            "wrongCount" to item.wrongCount,
            "pointsEarned" to item.pointsEarned,
            "starsEarned" to item.starsEarned,
            "accuracyPercentage" to item.accuracyPercentage,
            "sourceTitle" to item.sourceTitle,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(data).await()
        return docRef.id
    }

    suspend fun fetchHistoricQuizSessions(): List<QuizHistoryEntity> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("users").document(uid).collection("quizHistory").get().await()
        return snapshot.documents.mapNotNull { d ->
            val mode = d.getString("mode") ?: "MULTIPLE_CHOICE"
            val total = d.getLong("totalQuestions")?.toInt() ?: 0
            val correct = d.getLong("correctCount")?.toInt() ?: 0
            val points = d.getLong("pointsEarned")?.toInt() ?: 0
            val stars = d.getLong("starsEarned")?.toInt() ?: 0
            QuizHistoryEntity(
                id = d.id.hashCode().toLong(),
                timestamp = System.currentTimeMillis(),
                mode = mode,
                totalQuestions = total,
                correctCount = correct,
                wrongCount = d.getLong("wrongCount")?.toInt() ?: (total - correct),
                pointsEarned = points,
                starsEarned = stars,
                accuracyPercentage = d.getLong("accuracyPercentage")?.toInt() ?: 0,
                sourceTitle = d.getString("sourceTitle") ?: ""
            )
        }
    }

    suspend fun saveAvatarToCloud(avatar: com.example.ui.model.AvatarCustomization) {
        val uid = auth.currentUser?.uid ?: return
        val userDoc = db.collection("users").document(uid)
        val data = hashMapOf(
            "username" to avatar.username,
            "suitId" to avatar.suitId.id,
            "skinToneIndex" to avatar.skinToneIndex,
            "expressionIndex" to avatar.expressionIndex,
            "hairStyleIndex" to avatar.hairStyleIndex,
            "hairColorIndex" to avatar.hairColorIndex,
            "glassesIndex" to avatar.glassesIndex,
            "glassesColorIndex" to avatar.glassesColorIndex,
            "facialHairIndex" to avatar.facialHairIndex,
            "facialHairColorIndex" to avatar.facialHairColorIndex,
            "headwearIndex" to avatar.headwearIndex,
            "headwearColorIndex" to avatar.headwearColorIndex,
            "backgroundIndex" to avatar.backgroundIndex,
            "sceneryIndex" to avatar.sceneryIndex,
            "unlockedSuits" to avatar.unlockedSuits.toList(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userDoc.set(data, SetOptions.merge()).await()
    }
}
