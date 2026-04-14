package com.example.collegecompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class AttendanceStatus {
    PRESENT, ABSENT, CANCELLED
}

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val date: LocalDate,
    val status: AttendanceStatus
)