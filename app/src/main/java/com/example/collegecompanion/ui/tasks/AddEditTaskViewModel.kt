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
import com.example.collegecompanion.notification.NotificationScheduler   // ✅ ADD IMPORT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val notificationScheduler: NotificationScheduler,   // ✅ CORRECT
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Int = savedStateHandle.get<Int>("taskId") ?: -1
    val isEditing: Boolean get() = taskId != -1

    var title               by mutableStateOf("")
    var description         by mutableStateOf("")
    var dueDate             by mutableStateOf<Long?>(null)
    var taskType            by mutableStateOf(TaskType.ASSIGNMENT)
    var subject             by mutableStateOf("")
    var titleError          by mutableStateOf(false)
    var reminderDaysBefore  by mutableStateOf<Int?>(null)

    init {
        if (isEditing) {
            viewModelScope.launch {
                repository.getTaskById(taskId)?.let { t ->
                    title              = t.title
                    description        = t.description
                    dueDate            = t.dueDate
                    taskType           = t.taskType
                    subject            = t.subject ?: ""
                    reminderDaysBefore = t.reminderDaysBefore
                }
            }
        }
    }

    fun save(onSuccess: () -> Unit) {
        if (title.isBlank()) {
            titleError = true
            return
        }

        viewModelScope.launch {
            val task = Task(
                id                 = if (isEditing) taskId else 0,
                title              = title.trim(),
                description        = description.trim(),
                dueDate            = dueDate,
                taskType           = taskType,
                subject            = subject.trim().takeIf { it.isNotBlank() },
                isCompleted        = false,
                reminderDaysBefore = reminderDaysBefore
            )

            // ✅ FIRST save task
            repository.insertTask(task)

            // ✅ THEN schedule notifications
            notificationScheduler.schedule()

            onSuccess()
        }
    }
}