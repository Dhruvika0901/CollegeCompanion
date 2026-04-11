package com.example.collegecompanion.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TaskSummary(
    val total: Int     = 0,
    val completed: Int = 0,
    val pending: Int   = 0
    // highPriority removed — Task has no priority field
)

data class DashboardUiState(
    val isLoading: Boolean    = true,
    val taskSummary: TaskSummary = TaskSummary(),
    val upcomingTasks: List<Task> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = taskRepository.getAllTasks()
        .map { tasks ->
            val pending = tasks.filter { !it.isCompleted }
            DashboardUiState(
                isLoading = false,
                taskSummary = TaskSummary(
                    total     = tasks.size,
                    completed = tasks.count { it.isCompleted },
                    pending   = pending.size
                    // no highPriority — removed
                ),
                upcomingTasks = pending
                    .filter { it.dueDate != null }
                    .sortedBy { it.dueDate }
                    .take(3)
            )
        }
        .stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5_000),
            initialValue  = DashboardUiState(isLoading = true)
        )
}