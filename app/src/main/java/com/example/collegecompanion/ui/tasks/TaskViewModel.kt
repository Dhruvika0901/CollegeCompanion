//TaskViewModel.kt - ViewModel for managing tasks, including search, filter, and CRUD operations. Also handles notification scheduling after task changes.
package com.example.collegecompanion.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter {
    ALL, ASSIGNMENT, LAB_WORK, MINI_PROJECT
}

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val notificationScheduler: NotificationScheduler   // ← inject this
) : ViewModel() {

    // ── Current task being edited (null = new task) ───────────────────────────
    private var currentTaskId: Int? = null

    fun loadTask(taskId: Int) {
        currentTaskId = taskId
    }

    // ── Search & filter state ─────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()

    // ── Filtered + searched task list ─────────────────────────────────────────
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(
        repository.getAllTasks(),
        _searchQuery,
        _selectedFilter
    ) { allTasks, query, filter ->
        allTasks
            .filter { task ->
                when (filter) {
                    TaskFilter.ALL          -> true
                    TaskFilter.ASSIGNMENT   -> task.taskType == TaskType.ASSIGNMENT
                    TaskFilter.LAB_WORK     -> task.taskType == TaskType.LAB_WORK
                    TaskFilter.MINI_PROJECT -> task.taskType == TaskType.MINI_PROJECT
                }
            }
            .filter { task ->
                if (query.isBlank()) true
                else task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // ── Actions ───────────────────────────────────────────────────────────────
    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onFilterSelected(filter: TaskFilter) { _selectedFilter.value = filter }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun toggleComplete(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    fun saveTask(
        title: String,
        description: String,
        dueDate: Long?,
        taskType: TaskType,
        subject: String,
        reminderDaysBefore: Int?
    ) {
        viewModelScope.launch {
            val task = Task(
                id          = currentTaskId ?: 0,
                title       = title,
                description = description,
                dueDate     = dueDate,
                taskType    = taskType,
                subject     = subject,
                reminderDaysBefore = reminderDaysBefore
            )
            if (currentTaskId == null) repository.insertTask(task)   // ← use repository, not taskRepository
            else repository.updateTask(task)

            notificationScheduler.schedule()   // reschedule after save
        }
    }
}