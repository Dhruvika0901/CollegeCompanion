package com.example.collegecompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,          // ← Long, not Int
    val title: String,
    val subject: String = "",
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false
)