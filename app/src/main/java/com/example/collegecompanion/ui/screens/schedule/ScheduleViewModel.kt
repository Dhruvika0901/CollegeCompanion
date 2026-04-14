package com.example.collegecompanion.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.ClassSlotRepository
import com.example.collegecompanion.domain.model.ClassSlot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ScheduleUiState(
    val selectedDay: Int = 1,
    val slots: List<ClassSlot> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ClassSlotRepository
) : ViewModel() {

    private val todayDay: Int = LocalDate.now().dayOfWeek.value.coerceIn(1, 6)
    private val _selectedDay = MutableStateFlow(todayDay)

    val uiState: StateFlow<ScheduleUiState> = _selectedDay
        .flatMapLatest { day ->
            repository.getSlotsByDay(day).map { slots ->
                ScheduleUiState(selectedDay = day, slots = slots)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ScheduleUiState(selectedDay = todayDay, isLoading = true)
        )

    fun selectDay(day: Int) { _selectedDay.value = day }
    fun addSlot(slot: ClassSlot)    = viewModelScope.launch { repository.insertSlot(slot) }
    fun updateSlot(slot: ClassSlot) = viewModelScope.launch { repository.updateSlot(slot) }
    fun deleteSlot(slot: ClassSlot) = viewModelScope.launch { repository.deleteSlot(slot) }

    suspend fun getLastSlotForDay(day: Int): ClassSlot? =
        repository.getLastSlotForDay(day)
}