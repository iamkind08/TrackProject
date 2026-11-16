package com.example.track

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MentalTaskDao {

    @Query("SELECT * FROM mental_tasks WHERE userId = :userId AND date = :date ORDER BY isDone ASC, createdAt DESC")
    fun observeByUserAndDate(userId: Long, date: String): Flow<List<MentalTask>>

    @Query("SELECT COUNT(*) FROM mental_tasks WHERE userId = :userId AND date = :date")
    fun observeTotalCount(userId: Long, date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM mental_tasks WHERE userId = :userId AND date = :date AND isDone = 1")
    fun observeDoneCount(userId: Long, date: String): Flow<Int>

    @Query("SELECT * FROM mental_tasks WHERE userId = :userId AND isDone = 1 ORDER BY date DESC, createdAt DESC")
    fun observeHistory(userId: Long): Flow<List<MentalTask>>

    @Insert
    suspend fun insert(task: MentalTask)

    @Update
    suspend fun update(task: MentalTask)

    @Delete
    suspend fun delete(task: MentalTask)

    @Query("SELECT COUNT(*) FROM mental_tasks WHERE userId = :userId AND date = :date AND isDone = 1")
    suspend fun countDoneToday(userId: Long, date: String): Int
}