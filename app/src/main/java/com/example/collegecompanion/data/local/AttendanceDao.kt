//AttendanceDao.kt
package com.example.collegecompanion.data.local

import androidx.room.*
import com.example.collegecompanion.domain.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE subjectName = :subject ORDER BY date DESC")
    fun getRecordsForSubject(subject: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AttendanceRecord)

    @Update
    suspend fun update(record: AttendanceRecord)

    @Delete
    suspend fun delete(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE subjectName = :subject AND date = :date")
    suspend fun deleteBySubjectAndDate(subject: String, date: String)
}