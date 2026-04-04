package com.example.collegecompanion.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>("taskId")
        ?.takeIf { it != -1L }

    val isEditing: Boolean get() = taskId != null

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    init {
        taskId?.let { id ->
            viewModelScope.launch {
                repository.getTaskById(id)?.let { task ->
                    _formState.value = TaskFormState(
                        title    = task.title,
                        subject  = task.subject,
                        dueDate  = task.dueDate,
                        priority = task.priority
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _formState.update { it.copy(title = title) }
    }

    fun onSubjectChange(subject: String) {
        _formState.update { it.copy(subject = subject) }
    }

    fun onDueDateChange(millis: Long?) {
        _formState.update { it.copy(dueDate = millis) }
    }

    fun onPriorityChange(priority: Priority) {
        _formState.update { it.copy(priority = priority) }
    }

    fun saveTask(onSuccess: () -> Unit) = viewModelScope.launch {
        val state = _formState.value
        if (state.title.isBlank()) {
            _formState.update { it.copy(titleError = "Title is required") }
            return@launch
        }
        val task = Task(
            id          = taskId ?: 0L,
            title       = state.title,
            subject     = state.subject,
            dueDate     = state.dueDate,
            priority    = state.priority,
            isCompleted = false
        )
        if (taskId == null) repository.insertTask(task)
        else                repository.updateTask(task)
        onSuccess()
    }
}

data class TaskFormState(
    val title      : String   = "",
    val subject    : String   = "",
    val dueDate    : Long?    = null,
    val priority   : Priority = Priority.MEDIUM,
    val titleError : String?  = null
)