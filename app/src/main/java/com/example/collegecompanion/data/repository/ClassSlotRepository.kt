package com.example.collegecompanion.data.repository

import com.example.collegecompanion.domain.model.ClassSlot
import kotlinx.coroutines.flow.Flow

interface ClassSlotRepository {
    fun getSlotsByDay(day: Int): Flow<List<ClassSlot>>
    fun getAllSlots(): Flow<List<ClassSlot>>
    suspend fun insertSlot(slot: ClassSlot)
    suspend fun getLastSlotForDay(day: Int): ClassSlot?
    suspend fun updateSlot(slot: ClassSlot)
    suspend fun deleteSlot(slot: ClassSlot)
}