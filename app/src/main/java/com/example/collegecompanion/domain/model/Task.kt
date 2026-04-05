package com.example.collegecompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// New enum for task type
enum class TaskType {
    GENERAL,        // default — existing tasks won't break
    ASSIGNMENT,
    LAB_WORK,
    MINI_PROJECT
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val taskType: TaskType = TaskType.GENERAL,  // ← NEW field
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)