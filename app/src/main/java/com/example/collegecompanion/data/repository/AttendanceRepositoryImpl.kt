//AttendanceRepositoryImpl.kt
package com.example.collegecompanion.data.repository

import com.example.collegecompanion.data.local.AttendanceDao
import com.example.collegecompanion.domain.model.AttendanceRecord
import com.example.collegecompanion.domain.model.AttendanceStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val dao: AttendanceDao
) : AttendanceRepository {

    override fun getAllRecords(): Flow<List<AttendanceRecord>> =
        dao.getAllRecords()

    override fun getRecordsForSubject(subject: String): Flow<List<AttendanceRecord>> =
        dao.getRecordsForSubject(subject)

    override suspend fun markAttendance(
        subject: String,
        date: LocalDate,
        status: AttendanceStatus
    ) {
        dao.insert(
            AttendanceRecord(
                subjectName = subject,
                date = date,
                status = status
            )
        )
    }

    override suspend fun delete(record: AttendanceRecord) =
        dao.delete(record)
}