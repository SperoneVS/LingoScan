package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizHistoryDao {
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<QuizHistoryEntity>>

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<QuizHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: QuizHistoryEntity): Long

    @Query("SELECT SUM(starsEarned) FROM quiz_history")
    fun getTotalStars(): Flow<Int?>

    @Query("SELECT SUM(pointsEarned) FROM quiz_history")
    fun getTotalPoints(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM quiz_history")
    fun getTotalQuizSessions(): Flow<Int>
}
