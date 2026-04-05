package com.example.collegecompanion.data.local

import androidx.room.*
import com.example.collegecompanion.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ── Read ──────────────────────────────────────────────────────────────
    @Query("SELECT * FROM tasks ORDER BY id DESC")          // removed createdAt
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")    // removed duplicate
    suspend fun getTaskById(id: Long): Task?                 // Long, not Int

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY id DESC")  // removed createdAt
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay")
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    // ── Write ─────────────────────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) // Long, not Int

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()
}