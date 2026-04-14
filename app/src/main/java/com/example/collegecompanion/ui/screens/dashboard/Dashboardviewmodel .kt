package com.example.collegecompanion.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.AttendanceRepository
import com.example.collegecompanion.data.repository.ClassSlotRepository
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.AttendanceStatus
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.collegecompanion.domain.model.ClassType
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class TaskSummary(
    val total: Int     = 0,
    val completed: Int = 0,
    val pending: Int   = 0
)

data class AttendanceDashboardSummary(
    val hasData: Boolean = false,
    val overallPercentage: Float = 0f,
    val subjectsBelowThreshold: Int = 0
)

data class DashboardUiState(
    val isLoading: Boolean                            = true,
    val taskSummary: TaskSummary                      = TaskSummary(),
    val upcomingTasks: List<Task>                     = emptyList(),
    val nextClass: ClassSlot?                         = null,
    val hasAnySlots: Boolean                          = false,   // ← ADD
    val attendanceSummary: AttendanceDashboardSummary = AttendanceDashboardSummary()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val classSlotRepository: ClassSlotRepository,
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        taskRepository.getAllTasks(),
        classSlotRepository.getAllSlots(),
        attendanceRepository.getAllRecords()
    ) { tasks: List<Task>,
        slots: List<ClassSlot>,
        records: List<com.example.collegecompanion.domain.model.AttendanceRecord> ->

        // ── Task summary ──────────────────────────────────────────────────────
        val pending = tasks.filter { !it.isCompleted }
        val taskSummary = TaskSummary(
            total     = tasks.size,
            completed = tasks.count { it.isCompleted },
            pending   = pending.size
        )
        val upcomingTasks = pending
            .filter { it.dueDate != null }
            .sortedBy { it.dueDate }
            .take(3)

        // ── Next class (real-time) ────────────────────────────────────────────
        // ClassSlot.dayOfWeek: 1=Monday … 7=Sunday (match your enum)
        // ClassSlot.startTime: minutes since midnight
        // Inside your combine lambda, replace the nextClass block with this:
        // ── Next class (real-time) ────────────────────────────────────────────
        val now        = LocalTime.now()
        val nowMinutes = now.hour * 60 + now.minute
        val todayInt   = LocalDate.now().dayOfWeek.value  // 1=Mon…7=Sun, matches ClassSlot

        val nextClass = slots
            .filter { it.classType != ClassType.BREAK }  // exclude breaks
            .mapNotNull { slot ->
                val daysUntil = when {
                    // Later today
                    slot.dayOfWeek == todayInt && slot.startTime > nowMinutes -> 0
                    // Earlier today (already passed) → next week
                    slot.dayOfWeek == todayInt && slot.startTime <= nowMinutes -> 7
                    // Future day this week (dayOfWeek > today)
                    slot.dayOfWeek > todayInt -> slot.dayOfWeek - todayInt
                    // Past day this week (dayOfWeek < today) → next week
                    else -> 7 - todayInt + slot.dayOfWeek
                }
                val sortKey = daysUntil * 1440 + slot.startTime
                Pair(sortKey, slot)
            }
            .minByOrNull { it.first }
            ?.second

        // ── Attendance summary ────────────────────────────────────────────────
        val subjects = slots.map { it.subject }.distinct()
        val attendanceSummary = if (records.isEmpty() || subjects.isEmpty()) {
            AttendanceDashboardSummary(hasData = false)
        } else {
            var totalPresent = 0
            var totalClasses = 0
            var belowThreshold = 0

            subjects.forEach { subject ->
                val subjectRecords = records.filter { it.subjectName == subject }
                val present = subjectRecords.count { it.status == AttendanceStatus.PRESENT }
                val absent  = subjectRecords.count { it.status == AttendanceStatus.ABSENT }
                val total   = present + absent  // cancelled excluded
                if (total > 0) {
                    totalPresent += present
                    totalClasses += total
                    val pct = (present.toFloat() / total) * 100f
                    if (pct < 75f) belowThreshold++
                }
            }

            val overallPct = if (totalClasses == 0) 0f
            else (totalPresent.toFloat() / totalClasses) * 100f

            AttendanceDashboardSummary(
                hasData               = totalClasses > 0,
                overallPercentage     = overallPct,
                subjectsBelowThreshold = belowThreshold
            )
        }

        DashboardUiState(
            isLoading         = false,
            taskSummary       = taskSummary,
            upcomingTasks     = upcomingTasks,
            nextClass         = nextClass,
            hasAnySlots       = slots.any { it.classType != ClassType.BREAK },  // ← ADD
            attendanceSummary = attendanceSummary
        )
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(isLoading = true)
    )
}