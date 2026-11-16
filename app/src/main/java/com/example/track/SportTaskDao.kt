package com.example.track

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SportTaskDao {

    @Query("SELECT * FROM sport_tasks WHERE userId = :userId AND date = :date ORDER BY isDone ASC, createdAt DESC")
    fun observeByUserAndDate(userId: Long, date: String): Flow<List<SportTask>>

    @Query("SELECT COUNT(*) FROM sport_tasks WHERE userId = :userId AND date = :date")
    fun observeTotalCount(userId: Long, date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM sport_tasks WHERE userId = :userId AND date = :date AND isDone = 1")
    fun observeDoneCount(userId: Long, date: String): Flow<Int>

    @Query("SELECT * FROM sport_tasks WHERE userId = :userId AND isDone = 1 ORDER BY date DESC, createdAt DESC")
    fun observeHistory(userId: Long): Flow<List<SportTask>>

    @Insert
    suspend fun insert(task: SportTask)

    @Update
    suspend fun update(task: SportTask)

    @Delete
    suspend fun delete(task: SportTask)

    @Query("SELECT COUNT(*) FROM sport_tasks WHERE userId = :userId AND date = :date AND isDone = 1")
    suspend fun countDoneToday(userId: Long, date: String): Int
}