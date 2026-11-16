package com.example.track

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<User>>

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun findByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): User?

    @Query("UPDATE users SET points = points + :delta WHERE id = :id")
    suspend fun addPoints(id: Long, delta: Int)

    @Query("UPDATE users SET avatar = :avatar WHERE id = :id")
    suspend fun setAvatar(id: Long, avatar: String)

    @Query("UPDATE users SET lastBonusDate = :date WHERE id = :id")
    suspend fun setLastBonusDate(id: Long, date: String)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<User?>
}

