// com/example/collegecompanion/data/repository/TaskRepository.kt
package com.example.collegecompanion.data.repository

import com.example.collegecompanion.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Int): Task?
    suspend fun insertTask(task: Task)   // Unit return — impl discards the Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}