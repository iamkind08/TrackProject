package com.example.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "career_tasks")
data class CareerTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val date: String = today()
)