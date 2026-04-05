package com.example.collegecompanion.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.domain.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onNavigateToTasks: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},   // placeholder — wire when screen exists
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item { GreetingCard() }

        item {
            TaskSummaryCard(
                summary      = uiState.taskSummary,
                onViewAllClick = onNavigateToTasks
            )
        }

        item {
            UpcomingTasksCard(
                tasks        = uiState.upcomingTasks,
                onViewAllClick = onNavigateToTasks
            )
        }

        item { AttendancePlaceholderCard(onNavigateToAttendance) }

        item { NextClassPlaceholderCard() }
    }
}

// ─── Greeting ────────────────────────────────────────────────────────────────

@Composable
private fun GreetingCard() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text  = greeting,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = "Here's your day at a glance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── Task Summary ─────────────────────────────────────────────────────────────

@Composable
private fun TaskSummaryCard(
    summary: TaskSummary,
    onViewAllClick: () -> Unit
) {
    DashboardCard(
        title       = "Tasks",
        actionLabel = "View all",
        onActionClick = onViewAllClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip("Total",   summary.total,        MaterialTheme.colorScheme.primary)
            StatChip("Done",    summary.completed,    Color(0xFF4CAF50))
            StatChip("Pending", summary.pending,      Color(0xFFFF9800))
            StatChip("Urgent",  summary.highPriority, Color(0xFFF44336))
        }
    }
}

@Composable
private fun StatChip(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = value.toString(),
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color      = color
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Upcoming Tasks ───────────────────────────────────────────────────────────

@Composable
private fun UpcomingTasksCard(
    tasks: List<Task>,
    onViewAllClick: () -> Unit
) {
    DashboardCard(
        title       = "Upcoming Tasks",
        actionLabel = if (tasks.isNotEmpty()) "View all" else null,
        onActionClick = onViewAllClick
    ) {
        if (tasks.isEmpty()) {
            EmptyState(Icons.Default.CheckCircle, "No upcoming tasks — you're all clear!")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEachIndexed { index, task ->
                    UpcomingTaskRow(task)
                    if (index < tasks.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingTaskRow(task: Task) {
    // Format epoch millis → "Jan 5" style
    val dateLabel = task.dueDate?.let { millis ->
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    } ?: "No date"

    // Map Priority enum to color
    val priorityColor = when (task.priority) {
        Priority.HIGH   -> Color(0xFFF44336)
        Priority.MEDIUM -> Color(0xFFFF9800)
        Priority.LOW    -> Color(0xFF4CAF50)
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small colored priority dot
        Surface(
            modifier = Modifier.size(10.dp),
            color    = priorityColor,
            shape    = MaterialTheme.shapes.extraSmall
        ) {}

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text       = task.title,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text  = task.priority.name
                    .lowercase()
                    .replaceFirstChar { it.uppercase() } + " priority",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text  = dateLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─── Placeholder Cards ────────────────────────────────────────────────────────

@Composable
private fun AttendancePlaceholderCard(onNavigateToAttendance: () -> Unit) {
    DashboardCard(
        title       = "Attendance",
        actionLabel = "Set up",
        onActionClick = onNavigateToAttendance
    ) {
        EmptyState(Icons.Default.BarChart, "No attendance data yet")
    }
}

@Composable
private fun NextClassPlaceholderCard() {
    DashboardCard(title = "Next Class", actionLabel = null, onActionClick = {}) {
        EmptyState(Icons.Default.Schedule, "No timetable available")
    }
}

// ─── Shared Helpers ───────────────────────────────────────────────────────────

@Composable
private fun DashboardCard(
    title: String,
    actionLabel: String?,
    onActionClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (actionLabel != null) {
                    TextButton(
                        onClick        = onActionClick,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(actionLabel, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector     = icon,
            contentDescription = null,
            tint            = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier        = Modifier.size(36.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}