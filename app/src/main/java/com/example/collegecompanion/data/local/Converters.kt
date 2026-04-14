// com/example/collegecompanion/data/local/Converters.kt
package com.example.collegecompanion.data.local

import androidx.room.TypeConverter
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.AttendanceStatus
import java.time.LocalDate
class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    // AttendanceStatus ↔ String
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus =
        AttendanceStatus.valueOf(value)
}