package com.example.collegecompanion.data.local

import androidx.room.*
import androidx.room.Query
import com.example.collegecompanion.domain.model.ClassSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassSlotDao {

    @Query("SELECT * FROM class_slots WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getSlotsByDay(day: Int): Flow<List<ClassSlot>>

    @Query("SELECT * FROM class_slots ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSlots(): Flow<List<ClassSlot>>

    @Query("SELECT * FROM class_slots WHERE dayOfWeek = :day ORDER BY endTime DESC LIMIT 1")
    suspend fun getLastSlotForDay(day: Int): ClassSlot?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: ClassSlot)

    @Update
    suspend fun updateSlot(slot: ClassSlot)

    @Delete
    suspend fun deleteSlot(slot: ClassSlot)
}