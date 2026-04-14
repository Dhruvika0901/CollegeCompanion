// DashboardScreen.kt
package com.example.collegecompanion.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.domain.model.Task
import java.text.SimpleDateFormat
import com.example.collegecompanion.domain.model.ClassSlot
import java.util.*

// ─── Binder Card Colors ───────────────────────────────────────────────────────
private val BinderBlue    = Color(0xFFB8C9F0)   // Tasks
private val BinderSalmon  = Color(0xFFF2A89A)   // Upcoming Tasks
private val BinderMint    = Color(0xFFA8DFC4)   // Attendance
private val BinderYellow  = Color(0xFFF5E49A)   // Next Class

private val BinderBlueDark   = Color(0xFF5C7EC7)
private val BinderSalmonDark = Color(0xFFD95F4B)
private val BinderMintDark   = Color(0xFF3A9E72)
private val BinderYellowDark = Color(0xFFB89A10)

@Composable
fun DashboardScreen(
    onNavigateToTasks: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
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
            .background(Color(0xFFF0F2F8))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        item { GreetingCard() }

        item {
            BinderCard(
                title         = "Tasks",
                cardColor     = BinderBlue,
                accentColor   = BinderBlueDark,
                actionLabel   = "View all",
                onActionClick = onNavigateToTasks
            ) {
                TaskSummaryContent(summary = uiState.taskSummary)
            }
        }

        item {
            BinderCard(
                title         = "Upcoming Tasks",
                cardColor     = BinderSalmon,
                accentColor   = BinderSalmonDark,
                actionLabel   = if (uiState.upcomingTasks.isNotEmpty()) "View all" else null,
                onActionClick = onNavigateToTasks
            ) {
                UpcomingTasksContent(tasks = uiState.upcomingTasks)
            }
        }

        item {
            BinderCard(
                title         = "Attendance",
                cardColor     = BinderMint,
                accentColor   = BinderMintDark,
                actionLabel   = if (uiState.attendanceSummary.hasData) "View all" else "Set up",
                onActionClick = onNavigateToAttendance
            ) {
                AttendanceSummaryContent(summary = uiState.attendanceSummary)
            }
        }

        item {
            BinderCard(
                title         = "Next Class",
                cardColor     = BinderYellow,
                accentColor   = BinderYellowDark,
                actionLabel   = null,
                onActionClick = {}
            ) {
                NextClassContent(
                    nextClass            = uiState.nextClass,
                    hasAnySlots          = uiState.hasAnySlots,
                    onNavigateToSchedule = onNavigateToSchedule
                )
            }
        }
    }
}

// ─── Greeting ────────────────────────────────────────────────────────────────

@Composable
private fun GreetingCard() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning ☀️"
        hour < 17 -> "Good afternoon 👋"
        else      -> "Good evening 🌙"
    }
    Column(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)) {
        Text(
            text       = greeting,
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color      = Color(0xFF1A1A2E)
        )
        Text(
            text  = "Here's your day at a glance",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280)
        )
    }
}

// ─── Binder Card Shell ────────────────────────────────────────────────────────

@Composable
private fun BinderCard(
    title: String,
    cardColor: Color,
    accentColor: Color,
    actionLabel: String?,
    onActionClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardColor)
    ) {
        // Left accent stripe (binder ring strip feel)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
                .background(accentColor)
                .align(Alignment.CenterStart)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            // Card header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 17.sp,
                    color      = Color(0xFF1A1A2E)
                )
                if (actionLabel != null) {
                    TextButton(
                        onClick        = onActionClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text  = actionLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

// ─── Task Summary Content ─────────────────────────────────────────────────────

@Composable
private fun TaskSummaryContent(summary: TaskSummary) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BinderStatChip("Total",   summary.total,     BinderBlueDark)
        BinderStatChip("Done",    summary.completed, Color(0xFF2E7D32))
        BinderStatChip("Pending", summary.pending,   Color(0xFFB85C00))
    }
}

@Composable
private fun BinderStatChip(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.45f))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text       = value.toString(),
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color      = color
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF4B5563)
        )
    }
}

// ─── Upcoming Tasks Content ───────────────────────────────────────────────────

@Composable
private fun UpcomingTasksContent(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        BinderEmptyState(Icons.Default.CheckCircle, "No upcoming tasks — you're all clear!")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEachIndexed { index, task ->
                UpcomingTaskRow(task)
                if (index < tasks.lastIndex) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun UpcomingTaskRow(task: Task) {
    val dateLabel = task.dueDate?.let { millis ->
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    } ?: "No date"

    val typeColor = when (task.taskType) {
        TaskType.GENERAL      -> Color(0xFF9E9E9E)
        TaskType.ASSIGNMENT   -> Color(0xFF2196F3)
        TaskType.LAB_WORK     -> Color(0xFF4CAF50)
        TaskType.MINI_PROJECT -> Color(0xFFFF9800)
    }
    val typeLabel = when (task.taskType) {
        TaskType.GENERAL      -> "General"
        TaskType.ASSIGNMENT   -> "Assignment"
        TaskType.LAB_WORK     -> "Lab Work"
        TaskType.MINI_PROJECT -> "Mini Project"
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(10.dp),
            color    = typeColor,
            shape    = MaterialTheme.shapes.extraSmall
        ) {}
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text       = task.title,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF1A1A2E)
            )
            Text(
                text  = typeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280)
            )
        }
        Text(
            text  = dateLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = BinderSalmonDark
        )
    }
}

// ─── Attendance Content ───────────────────────────────────────────────────────

@Composable
private fun AttendanceSummaryContent(summary: AttendanceDashboardSummary) {
    if (!summary.hasData) {
        BinderEmptyState(Icons.Default.BarChart, "No attendance data yet")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text       = "%.0f%% overall".format(summary.overallPercentage),
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color      = if (summary.overallPercentage >= 75f)
                    Color(0xFF1B5E20) else Color(0xFFB71C1C)
            )
            if (summary.subjectsBelowThreshold > 0) {
                Text(
                    text  = "⚠ ${summary.subjectsBelowThreshold} subject${
                        if (summary.subjectsBelowThreshold > 1) "s" else ""
                    } below 75%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB71C1C)
                )
            } else {
                Text(
                    text  = "✓ All subjects above 75%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B5E20)
                )
            }
        }
    }
}

// ─── Next Class Content ───────────────────────────────────────────────────────

@Composable
private fun NextClassContent(
    nextClass: ClassSlot?,
    hasAnySlots: Boolean,
    onNavigateToSchedule: () -> Unit
) {
    when {
        !hasAnySlots -> {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint               = Color(0xFF1A1A2E).copy(alpha = 0.35f),
                    modifier           = Modifier.size(36.dp)
                )
                Text(
                    text  = "No timetable yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4B5563)
                )
                OutlinedButton(
                    onClick = onNavigateToSchedule,
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = BinderYellowDark)
                ) {
                    Text("Set up timetable", fontWeight = FontWeight.Bold)
                }
            }
        }

        nextClass == null -> {
            BinderEmptyState(Icons.Default.Schedule, "No upcoming classes scheduled")
        }

        else -> {
            val hours   = nextClass.startTime / 60
            val minutes = nextClass.startTime % 60
            val amPm    = if (hours < 12) "AM" else "PM"
            val hour12  = when {
                hours == 0 -> 12
                hours > 12 -> hours - 12
                else       -> hours
            }
            val timeStr = "%d:%02d %s".format(hour12, minutes, amPm)
            val days    = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
            val dayStr  = days.getOrElse(nextClass.dayOfWeek - 1) { "" }

            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text       = nextClass.subject,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color(0xFF1A1A2E)
                    )
                    Text(
                        text  = nextClass.room,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text       = timeStr,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = BinderYellowDark
                    )
                    Text(
                        text  = dayStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

// ─── Shared Binder Helpers ────────────────────────────────────────────────────

@Composable
private fun BinderEmptyState(icon: ImageVector, message: String) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color(0xFF1A1A2E).copy(alpha = 0.3f),
            modifier           = Modifier.size(32.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280)
        )
    }
}