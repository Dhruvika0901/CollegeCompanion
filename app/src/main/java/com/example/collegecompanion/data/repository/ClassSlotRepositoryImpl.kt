// com/example/collegecompanion/data/repository/ClassSlotRepository.kt
package com.example.collegecompanion.data.repository

import com.example.collegecompanion.data.local.ClassSlotDao
import com.example.collegecompanion.domain.model.ClassSlot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClassSlotRepositoryImpl @Inject constructor(
    private val dao: ClassSlotDao
) : ClassSlotRepository {
    override fun getSlotsByDay(day: Int): Flow<List<ClassSlot>> = dao.getSlotsByDay(day)
    override fun getAllSlots(): Flow<List<ClassSlot>> = dao.getAllSlots()
    override suspend fun insertSlot(slot: ClassSlot) = dao.insertSlot(slot)
    override suspend fun updateSlot(slot: ClassSlot) = dao.updateSlot(slot)
    override suspend fun deleteSlot(slot: ClassSlot) = dao.deleteSlot(slot)
}