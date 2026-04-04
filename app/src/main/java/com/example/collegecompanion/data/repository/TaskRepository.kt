// com/example/collegecompanion/data/repository/TaskRepository.kt
package com.example.collegecompanion.data.repository

import com.example.collegecompanion.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getPendingTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksBySubject(subject: String): Flow<List<Task>>
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>>
//  suspend fun getTaskById(taskId: Int): Task?
    suspend fun getTaskById(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
    suspend fun deleteAllCompletedTasks()
}