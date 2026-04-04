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

    override fun getPendingTasks(): Flow<List<Task>> =
        taskDao.getPendingTasks()

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks()

    override fun getTasksBySubject(subject: String): Flow<List<Task>> =
        taskDao.getTasksBySubject(subject)

    override fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.getTasksDueToday(startOfDay, endOfDay)

    // TaskRepositoryImpl.kt
    override suspend fun getTaskById(id: Long): Task? {   // ← Long here
        return taskDao.getTaskById(id)
    }

    override suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(task)

    override suspend fun updateTask(task: Task) =
        taskDao.updateTask(task)

    override suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(task)

    override suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) =
        taskDao.updateTaskCompletion(taskId, isCompleted)

    override suspend fun deleteAllCompletedTasks() =
        taskDao.deleteAllCompletedTasks()
}