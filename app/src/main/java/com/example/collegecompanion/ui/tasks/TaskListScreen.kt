package com.example.collegecompanion.ui.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks          by viewModel.tasks.collectAsStateWithLifecycle()
    val searchQuery    by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Task") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                onClick = { navController.navigate(Screen.AddTask.route) }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            // ── Search Bar ────────────────────────────────────────────────────
            SearchBar(
                query    = searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            Spacer(Modifier.height(12.dp))

            // ── Filter Chips ──────────────────────────────────────────────────
            FilterChipRow(
                selected  = selectedFilter,
                onSelect  = viewModel::onFilterSelected
            )

            Spacer(Modifier.height(8.dp))

            // ── Task Count label ──────────────────────────────────────────────
            Text(
                text  = "${tasks.size} task${if (tasks.size != 1) "s" else ""}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // ── Task List or Empty State ───────────────────────────────────────
            if (tasks.isEmpty()) {
                TaskEmptyState(
                    isFiltered = searchQuery.isNotBlank() ||
                            selectedFilter != TaskFilter.ALL
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding      = PaddingValues(bottom = 80.dp) // FAB clearance
                ) {
                    items(
                        items = tasks,
                        key   = { it.id }   // stable keys = smooth animations
                    ) { task ->
                        SwipeToDeleteTaskItem(
                            task       = task,
                            onDelete   = { viewModel.deleteTask(task) },
                            onToggle   = { viewModel.toggleComplete(task) },
                            onEdit = {
                                navController.navigate(
                                    Screen.AddTask.editRoute(task.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        modifier      = Modifier.fillMaxWidth(),
        placeholder   = { Text("Search tasks...") },
        leadingIcon   = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon  = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine    = true,
        shape         = MaterialTheme.shapes.large
    )
}

// ─── Filter Chips ─────────────────────────────────────────────────────────────

@Composable
private fun FilterChipRow(
    selected: TaskFilter,
    onSelect: (TaskFilter) -> Unit
) {
    // Map each filter to a display label
    val filters = listOf(
        TaskFilter.ALL          to "All",
        TaskFilter.ASSIGNMENT   to "Assignment",
        TaskFilter.LAB_WORK     to "Lab Work",
        TaskFilter.MINI_PROJECT to "Mini Project"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (filter, label) ->
            FilterChip(
                selected = selected == filter,
                onClick  = { onSelect(filter) },
                label    = { Text(label) }
            )
        }
    }
}

// ─── Swipe to Delete Wrapper ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTaskItem(
    task: Task,
    onDelete: () -> Unit,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state            = dismissState,
        enableDismissFromStartToEnd = false,    // only swipe left to delete
        backgroundContent = {
            // Red delete background shown while swiping
            val color by animateColorAsState(
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    Color(0xFFF44336) else Color.Transparent,
                label = "swipe_bg"
            )
            val scale by animateFloatAsState(
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    1f else 0.75f,
                label = "swipe_icon_scale"
            )
            Box(
                modifier          = Modifier
                    .fillMaxSize()
                    .background(color, shape = MaterialTheme.shapes.medium)
                    .padding(end = 20.dp),
                contentAlignment  = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint               = Color.White,
                    modifier           = Modifier.scale(scale)
                )
            }
        }
    ) {
        TaskCard(
            task     = task,
            onToggle = onToggle,
            onEdit   = onEdit
        )
    }
}

// ─── Task Card ────────────────────────────────────────────────────────────────

@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        onClick   = onEdit,
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion checkbox
            Checkbox(
                checked         = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Spacer(Modifier.width(8.dp))

            // Task info
            Column(Modifier.weight(1f)) {
                Text(
                    text           = task.title,
                    style          = MaterialTheme.typography.bodyLarge,
                    fontWeight     = FontWeight.Medium,
                    maxLines       = 1,
                    overflow       = TextOverflow.Ellipsis,
                    // Strike-through when completed
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                // Badge row: task type + priority + due date
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Task type badge (only show if not GENERAL)
                    if (task.taskType != `TaskType.kt`.GENERAL) {
                        TaskTypeBadge(task.taskType)
                    }

                    PriorityBadge(task.priority)

                    task.dueDate?.let { millis ->
                        val formatted = SimpleDateFormat(
                            "MMM d", Locale.getDefault()
                        ).format(Date(millis))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector        = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier           = Modifier.size(12.dp),
                                tint               = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text  = formatted,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Badge Composables ────────────────────────────────────────────────────────

@Composable
private fun TaskTypeBadge(type: `TaskType.kt`) {
    val (label, color) = when (type) {
        `TaskType.kt`.ASSIGNMENT   -> "Assignment"   to Color(0xFF2196F3)
        `TaskType.kt`.LAB_WORK     -> "Lab Work"     to Color(0xFF9C27B0)
        `TaskType.kt`.MINI_PROJECT -> "Mini Project" to Color(0xFF009688)
        `TaskType.kt`.GENERAL      -> "General"      to Color(0xFF607D8B)
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val (label, color) = when (priority) {
        Priority.HIGH   -> "High"   to Color(0xFFF44336)
        Priority.MEDIUM -> "Medium" to Color(0xFFFF9800)
        Priority.LOW    -> "Low"    to Color(0xFF4CAF50)
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun TaskEmptyState(isFiltered: Boolean) {
    Column(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = if (isFiltered) Icons.Default.SearchOff
            else Icons.Default.AssignmentLate,
            contentDescription = null,
            modifier           = Modifier.size(64.dp),
            tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Text(
            text  = if (isFiltered) "No tasks match your search"
            else "No tasks yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text  = if (isFiltered) "Try a different filter or search term"
            else "Tap the button below to add your first task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}