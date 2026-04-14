package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

private val Purple = Color(0xFF7B61FF)
private val LightBg = Color(0xFFF6F7FB)

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
        containerColor = LightBg,

        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple,
                    titleContentColor = Color.White
                ),
                title = {
                    Column {
                        Text("My Tasks", style = MaterialTheme.typography.titleLarge)
                        val pending = tasks.count { !it.isCompleted }
                        if (pending > 0) Text(
                            "$pending pending",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTask.newTaskRoute) },
                containerColor = Purple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .background(LightBg)
        ) {

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Search tasks…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Purple
                )
            )

            // Filters
            val filters = listOf(
                TaskFilter.ALL          to "All",
                TaskFilter.ASSIGNMENT   to "Assignment",
                TaskFilter.LAB_WORK     to "Lab Work",
                TaskFilter.MINI_PROJECT to "Mini Project"
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { (filter, label) ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick  = { viewModel.onFilterSelected(filter) },
                        label    = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isBlank()) "No tasks yet. Tap + to add one."
                        else "No tasks match \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task     = task,
                            onToggle = { viewModel.toggleComplete(task) },
                            onEdit   = {
                                navController.navigate(Screen.AddTask.editRoute(task.id))
                            },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { it == SwipeToDismissBoxValue.EndToStart }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) onDelete()
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.padding(end = 20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    ) {
        val containerColor = when (task.taskType) {
            TaskType.GENERAL      -> Color(0xFFF1F1F1)
            TaskType.ASSIGNMENT   -> Color(0xFFE6E0FF) // light purple
            TaskType.LAB_WORK     -> Color(0xFFDFF7F0) // light green
            TaskType.MINI_PROJECT -> Color(0xFFFFE9D6) // light orange
        }

        Card(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Purple
                    )
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskTypeBadge(task.taskType)

                        task.subject?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        task.dueDate?.let { due ->
                            val fmt = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(due))
                            val overdue = due < System.currentTimeMillis()

                            Text(
                                fmt,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (overdue && !task.isCompleted)
                                    MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskTypeBadge(type: TaskType) {
    val (label, container, content) = when (type) {
        TaskType.GENERAL      -> Triple("General", Color(0xFFEDEDED), Color.DarkGray)
        TaskType.ASSIGNMENT   -> Triple("Assignment", Color(0xFFDAD4FF), Purple)
        TaskType.LAB_WORK     -> Triple("Lab Work", Color(0xFFD4F5E9), Color(0xFF1BA97F))
        TaskType.MINI_PROJECT -> Triple("Mini Project", Color(0xFFFFE0CC), Color(0xFFFF8A3D))
    }

    Surface(
        color = container,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = content
        )
    }
}