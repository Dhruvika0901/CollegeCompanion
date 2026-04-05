package com.example.collegecompanion.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class TaskSummary(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val highPriority: Int = 0
)

data class DashboardUiState(
    val taskSummary: TaskSummary = TaskSummary(),
    val upcomingTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = taskRepository.getAllTasks()
        .map { tasks ->
            val nowMillis = System.currentTimeMillis()

            val summary = TaskSummary(
                total       = tasks.size,
                completed   = tasks.count { it.isCompleted },
                pending     = tasks.count { !it.isCompleted },
                // Priority enum — compare directly
                highPriority = tasks.count {
                    it.priority == Priority.HIGH && !it.isCompleted
                }
            )

            // Upcoming = incomplete tasks whose dueDate is today or future
            // dueDate is epoch millis — compare against start of today
            val startOfToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val upcoming = tasks
                .filter { task ->
                    !task.isCompleted &&
                            task.dueDate != null &&
                            task.dueDate >= startOfToday
                }
                .sortedBy { it.dueDate }
                .take(5)

            DashboardUiState(
                taskSummary  = summary,
                upcomingTasks = upcoming,
                isLoading    = false
            )
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState(isLoading = true)
        )
}