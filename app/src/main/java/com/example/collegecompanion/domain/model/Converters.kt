package com.example.collegecompanion.domain.model

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)
}