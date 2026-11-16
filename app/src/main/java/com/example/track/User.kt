package com.example.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val login: String,
    val password: String,
    val age: Int,
    val avatar: String = "avatar_1",
    val points: Int = 0,
    val lastBonusDate: String? = null
)