package com.example.collegecompanion.data.repository

import com.example.collegecompanion.domain.model.AttendanceRecord
import com.example.collegecompanion.domain.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {
    fun getAllRecords(): Flow<List<AttendanceRecord>>
    fun getRecordsForSubject(subject: String): Flow<List<AttendanceRecord>>
    suspend fun markAttendance(subject: String, date: LocalDate, status: AttendanceStatus)
    suspend fun delete(record: AttendanceRecord)
}