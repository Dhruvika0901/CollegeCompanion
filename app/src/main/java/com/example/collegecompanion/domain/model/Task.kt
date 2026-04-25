// com/example/collegecompanion/domain/model/Task.kt
package com.example.collegecompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ← no enum class TaskType here, it lives in TaskType.kt

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val taskType: TaskType = TaskType.ASSIGNMENT,
    val subject: String? = null,
    val isCompleted: Boolean = false,
    val reminderDaysBefore: Int? = null
)