package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.Task

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit
) {
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No tasks yet. Tap + to add one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task             = task,
                        onToggleComplete = { viewModel.toggleComplete(task) },
                        onDelete         = { viewModel.deleteTask(task) },
                        onEdit           = { onEditTask(task.id) }
                    )
                }
            }
        }
    }
}


//TaskCard(
//task = task,
//onToggleComplete = { viewModel.toggleComplete(task) },
//onDelete = { viewModel.deleteTask(task) },
//onEdit = { onEditTask(task.id) }   // task.id must be Long
//)
@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH   -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.primary
        Priority.LOW    -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick  = onEdit
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(                              // ← directly in Row, NOT inside IconButton
                checked         = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text           = task.title,
                    style          = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.subject.isNotBlank()) {
                    Text(
                        task.subject,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            SuggestionChip(
                onClick = {},
                label   = {
                    Text(task.priority.name, style = MaterialTheme.typography.labelSmall)
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = priorityColor.copy(alpha = 0.12f),
                    labelColor     = priorityColor
                )
            )
            IconButton(onClick = onDelete) {       // ← Delete button is its own IconButton
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}