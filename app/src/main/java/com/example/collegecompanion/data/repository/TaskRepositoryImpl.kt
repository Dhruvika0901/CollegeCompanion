// com/example/collegecompanion/data/repository/TaskRepositoryImpl.kt
package com.example.collegecompanion.data.repository

import com.example.collegecompanion.data.local.TaskDao
import com.example.collegecompanion.domain.model.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks()

    override suspend fun getTaskById(id: Int): Task? =
        taskDao.getTaskById(id)

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)          // Long return discarded — matches interface
    }

    override suspend fun updateTask(task: Task) =
        taskDao.updateTask(task)

    override suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(task)
}