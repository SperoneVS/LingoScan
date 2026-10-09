package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE documentId = :documentId ORDER BY createdAt DESC")
    fun getWordsForDocument(documentId: Long): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByName(word: String): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)

    @Query("UPDATE words SET correctCount = correctCount + 1, masteryLevel = CASE WHEN masteryLevel < 2 THEN masteryLevel + 1 ELSE 2 END WHERE id = :id")
    suspend fun recordCorrectAnswer(id: Long)

    @Query("UPDATE words SET wrongCount = wrongCount + 1, masteryLevel = CASE WHEN masteryLevel > 0 THEN masteryLevel - 1 ELSE 0 END WHERE id = :id")
    suspend fun recordWrongAnswer(id: Long)
}
