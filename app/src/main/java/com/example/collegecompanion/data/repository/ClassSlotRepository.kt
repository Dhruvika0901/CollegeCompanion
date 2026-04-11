package com.example.collegecompanion.data.repository

import com.example.collegecompanion.domain.model.ClassSlot  // ✅ domain.model not data.model
import kotlinx.coroutines.flow.Flow

interface ClassSlotRepository {
    fun getSlotsByDay(day: Int): Flow<List<ClassSlot>>
    fun getAllSlots(): Flow<List<ClassSlot>>
    suspend fun insertSlot(slot: ClassSlot)
    suspend fun updateSlot(slot: ClassSlot)
    suspend fun deleteSlot(slot: ClassSlot)
}