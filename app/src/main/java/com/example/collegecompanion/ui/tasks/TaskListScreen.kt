package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

/* ── Colour tokens ─────────────────────────────────────────────── */
private val BgPage       = Color(0xFFF5F6FA)
private val AccentPurple = Color(0xFF4B4ACF)
private val CardWhite    = Color.White
private val TextPrimary  = Color(0xFF1A1A2E)
private val TextMuted    = Color(0xFF9095A1)
private val DividerColor = Color(0xFFEEEFF4)

private val ChipUrgent   = Color(0xFFFF4D4F)
private val ChipProgress = Color(0xFF4B4ACF)
private val ChipMini     = Color(0xFF7B6CF6)
private val ChipPending  = Color(0xFFFFA726)
private val ChipGeneral  = Color(0xFF78909C)

/* ════════════════════════════════════════════════════════════════
   Screen
   ════════════════════════════════════════════════════════════════ */
@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks          by viewModel.tasks.collectAsStateWithLifecycle()
    val searchQuery    by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    val pendingCount = tasks.count { !it.isCompleted }

    Scaffold(
        containerColor = BgPage,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTask.newTaskRoute) },
                containerColor = AccentPurple,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            /* ── Header ─────────────────────────────── */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "My Tasks",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = TextPrimary
                )
                if (pendingCount > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$pendingCount tasks pending",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }

            /* ── Search ─────────────────────────────── */
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = {
                    Text(
                        "Search tasks…",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = DividerColor,
                    unfocusedContainerColor = CardWhite,
                    focusedContainerColor = CardWhite,
                    cursorColor = AccentPurple
                )
            )

            /* ── Filter chips ───────────────────────── */
            val filters = listOf(
                TaskFilter.ALL          to "All",
                TaskFilter.ASSIGNMENT   to "Assignments",
                TaskFilter.LAB_WORK     to "Lab Work",
                TaskFilter.MINI_PROJECT to "Mini Projects"
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                items(filters) { (filter, label) ->
                    val active = selectedFilter == filter
                    Surface(
                        onClick = { viewModel.onFilterSelected(filter) },
                        shape = RoundedCornerShape(50),
                        color = if (active) AccentPurple else CardWhite,
                        shadowElevation = if (active) 0.dp else 1.dp
                    ) {
                        Text(
                            text = label,
                            color = if (active) Color.White else TextMuted,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            /* ── List / Empty state ─────────────────── */
            if (tasks.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (searchQuery.isBlank()) "No tasks yet.\nTap + to add one."
                            else "No tasks match\n\"$searchQuery\"",
                            color = TextMuted,
                            fontSize = 15.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        SwipeableTaskCard(
                            task     = task,
                            onToggle = { viewModel.toggleComplete(task) },
                            onEdit   = { navController.navigate(Screen.AddTask.editRoute(task.id)) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) } // FAB clearance
                }
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Swipeable wrapper
   ════════════════════════════════════════════════════════════════ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskCard(
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
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFEBEB)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier.padding(end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "Delete",
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) {
        TaskCard(task = task, onToggle = onToggle, onEdit = onEdit)
    }
}

/* ════════════════════════════════════════════════════════════════
   Task Card
   ════════════════════════════════════════════════════════════════ */
@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val isMiniProject = task.taskType == TaskType.MINI_PROJECT

    if (isMiniProject) {
        MiniProjectTaskCard(task, onToggle, onEdit)
    } else {
        StandardTaskCard(task, onToggle, onEdit)
    }
}

/* ── Standard card ─────────────────────────────────────────────── */
@Composable
private fun StandardTaskCard(task: Task, onToggle: () -> Unit, onEdit: () -> Unit) {
    val (chipLabel, chipColor) = taskChipMeta(task)
    val dateText = task.dueDate?.let {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
    }
    val isOverdue = task.dueDate != null &&
            task.dueDate < System.currentTimeMillis() &&
            !task.isCompleted

    Card(
        onClick = onEdit,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            /* Chip row */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(chipLabel, chipColor)
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AccentPurple,
                        uncheckedColor = TextMuted
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            /* Accent bar + title */
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(42.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(chipColor)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (task.isCompleted) TextMuted else TextPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    task.subject?.let {
                        Text(
                            text = "Course: $it",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))

            /* Footer: date */
            dateText?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isOverdue) Color(0xFFE53935) else TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (isOverdue) "Overdue · $it" else it,
                        fontSize = 12.sp,
                        color = if (isOverdue) Color(0xFFE53935) else TextMuted
                    )
                }
            }
        }
    }
}

/* ── Mini Project card (purple) ────────────────────────────────── */
@Composable
private fun MiniProjectTaskCard(task: Task, onToggle: () -> Unit, onEdit: () -> Unit) {
    Card(
        onClick = onEdit,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(
                    label = "MINI PROJECT",
                    color = ChipMini,
                    textColor = Color.White,
                    outlined = true
                )
                Spacer(Modifier.weight(1f))
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White.copy(alpha = 0.6f),
                        checkmarkColor = AccentPurple
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = task.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (task.isCompleted) Color.White.copy(alpha = 0.5f) else Color.White,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                lineHeight = 26.sp
            )

            task.subject?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 2
                )
            }

            task.dueDate?.let { due ->
                val fmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(due))
                val isOverdue = due < System.currentTimeMillis() && !task.isCompleted
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isOverdue) Color(0xFFFFCDD2) else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (isOverdue) "Overdue · $fmt" else fmt,
                        fontSize = 12.sp,
                        color = if (isOverdue) Color(0xFFFFCDD2) else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Helpers
   ════════════════════════════════════════════════════════════════ */

@Composable
private fun StatusChip(
    label: String,
    color: Color,
    textColor: Color = Color.White,
    outlined: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (outlined) color.copy(alpha = 0.25f) else color
    ) {
        Text(
            text = label,
            color = if (outlined) Color.White else textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private fun taskChipMeta(task: Task): Pair<String, Color> = when (task.taskType) {
    TaskType.ASSIGNMENT   -> "ASSIGNMENT" to ChipUrgent
    TaskType.LAB_WORK     -> "LAB WORK"   to ChipProgress
    TaskType.MINI_PROJECT -> "MINI PROJECT" to ChipMini
    TaskType.GENERAL      -> "GENERAL"    to ChipGeneral
}