package com.example.collegecompanion.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegecompanion.data.repository.TaskRepository
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle           // ← Hilt injects this automatically
) : ViewModel() {

    // Reads "taskId" from the nav route  "add_task?taskId={taskId}"
    // defaultValue = -1 means "new task"
    private val taskId: Int = savedStateHandle.get<Int>("taskId") ?: -1
    val isEditing: Boolean get() = taskId != -1

    var title       by mutableStateOf("")
    var description by mutableStateOf("")
    var dueDate     by mutableStateOf<Long?>(null)
    var taskType    by mutableStateOf(TaskType.ASSIGNMENT)
    var subject     by mutableStateOf("")
    var titleError  by mutableStateOf(false)

    init {
        if (isEditing) {
            viewModelScope.launch {
                repository.getTaskById(taskId)?.let { t ->
                    title       = t.title
                    description = t.description
                    dueDate     = t.dueDate
                    taskType    = t.taskType
                    subject     = t.subject ?: ""
                }
            }
        }
    }

    fun save(onSuccess: () -> Unit) {
        if (title.isBlank()) { titleError = true; return }
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    id          = if (isEditing) taskId else 0,
                    title       = title.trim(),
                    description = description.trim(),
                    dueDate     = dueDate,
                    taskType    = taskType,
                    subject     = subject.trim().takeIf { it.isNotBlank() },
                    isCompleted = false
                )
            )
            onSuccess()
        }
    }
}