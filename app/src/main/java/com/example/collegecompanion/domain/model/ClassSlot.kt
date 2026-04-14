// com/example/collegecompanion/data/repository/ClassSlot.kt
package com.example.collegecompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_slots")
data class ClassSlot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: Int,          // 1 = Monday … 6 = Saturday
    val startTime: Int,          // minutes since midnight, e.g. 540 = 9:00 AM
    val endTime: Int,            // e.g. 600 = 10:00 AM
    val subject: String,
    val room: String,
    val professor: String? = null,
    val classType: ClassType = ClassType.LECTURE
)