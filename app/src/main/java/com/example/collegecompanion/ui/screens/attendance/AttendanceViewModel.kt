package com.example.collegecompanion.ui.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.AttendanceRepository
import com.example.collegecompanion.data.repository.ClassSlotRepository
import com.example.collegecompanion.domain.model.AttendanceRecord
import com.example.collegecompanion.domain.model.AttendanceStatus
import com.example.collegecompanion.domain.model.ClassType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class SubjectAttendanceSummary(
    val subject: String,
    val present: Int,
    val absent: Int,
    val cancelled: Int,
) {
    val total: Int get() = present + absent
    val percentage: Float get() = if (total == 0) 0f else (present.toFloat() / total) * 100f
    val isLow: Boolean get() = percentage < 75f && total > 0
}

data class AttendanceUiState(
    val summaries: List<SubjectAttendanceSummary> = emptyList(),
    val allRecords: List<AttendanceRecord> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepo: AttendanceRepository,
    private val classSlotRepo: ClassSlotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                classSlotRepo.getAllSlots(),
                attendanceRepo.getAllRecords()
            ) { slots: List<com.example.collegecompanion.domain.model.ClassSlot>,
                records: List<AttendanceRecord> ->

                // ✅ Exclude BREAK slots — breaks are never tracked for attendance
                val subjects: List<String> = slots
                    .filter { slot -> slot.classType != ClassType.BREAK }
                    .map { slot -> slot.subject }
                    .distinct()
                    .sorted()

                val summaries: List<SubjectAttendanceSummary> = subjects.map { subjectName ->
                    val subjectRecords = records.filter { it.subjectName == subjectName }
                    SubjectAttendanceSummary(
                        subject   = subjectName,
                        present   = subjectRecords.count { it.status == AttendanceStatus.PRESENT },
                        absent    = subjectRecords.count { it.status == AttendanceStatus.ABSENT },
                        cancelled = subjectRecords.count { it.status == AttendanceStatus.CANCELLED }
                    )
                }

                AttendanceUiState(
                    summaries  = summaries,
                    allRecords = records,
                    isLoading  = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun markAttendance(
        subject: String,
        status: AttendanceStatus,
        date: LocalDate = LocalDate.now()
    ) {
        viewModelScope.launch {
            attendanceRepo.markAttendance(subject, date, status)
        }
    }

    fun deleteRecord(record: AttendanceRecord) {
        viewModelScope.launch {
            attendanceRepo.delete(record)
        }
    }
}