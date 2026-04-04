// com/example/collegecompanion/data/local/Converters.kt
package com.example.collegecompanion.data.local

import androidx.room.TypeConverter
import com.example.collegecompanion.domain.model.Priority

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}