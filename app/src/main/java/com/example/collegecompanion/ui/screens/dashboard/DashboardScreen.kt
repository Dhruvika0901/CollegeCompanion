// DashboardScreen.kt
package com.example.collegecompanion.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import java.util.*
import kotlin.math.ceil
import kotlin.math.max

// ─── Design Tokens ────────────────────────────────────────────────────────────
private val AppBlue       = Color(0xFF4A6CF7)
private val AppBackground = Color(0xFFF4F5FA)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1F36)
private val TextSecondary = Color(0xFF8A94A6)
private val AlertRed      = Color(0xFFFF5C5C)
private val AlertRedBg    = Color(0xFFFFF0F0)
private val PendingPillBg = Color(0xFFEEF0FF)
private val PendingPillFg = Color(0xFF4A6CF7)
private val DividerColor  = Color(0xFFF0F1F5)
private val GreenOk       = Color(0xFF27AE60)

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
            CircularProgressIndicator(color = AppBlue)
        }
        return
    }

    // Derive today's class count from existing uiState fields.
    // Calendar.DAY_OF_WEEK: Sun=1, Mon=2…Sat=7
    // ClassSlot.dayOfWeek:  Mon=1, Tue=2…Sun=7
    val calDow     = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val slotDow    = if (calDow == Calendar.SUNDAY) 7 else calDow - 1
    val classCount = if (uiState.hasAnySlots && uiState.nextClass?.dayOfWeek == slotDow) 1 else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            GreetingCard(
                classCount = classCount,
                taskCount  = uiState.taskSummary.total
            )
        }

        // 1. Next Class — whole card tappable → Schedule
        item {
            BinderCard(
                title         = "Next Class",
                actionLabel   = "View Schedule",
                onActionClick = onNavigateToSchedule,
                onCardClick   = onNavigateToSchedule
            ) {
                NextClassContent(
                    nextClass            = uiState.nextClass,
                    hasAnySlots          = uiState.hasAnySlots,
                    onNavigateToSchedule = onNavigateToSchedule
                )
            }
        }

        // 2. Attendance
        item {
            BinderCard(
                title         = "Attendance",
                actionLabel   = if (uiState.attendanceSummary.hasData) "View all" else "Set up",
                onActionClick = onNavigateToAttendance
            ) {
                AttendanceSummaryContent(summary = uiState.attendanceSummary)
            }
        }

        // 3. Tasks
        item {
            BinderCard(
                title         = "TASKS",
                actionLabel   = "View all",
                onActionClick = onNavigateToTasks
            ) {
                TaskSummaryContent(summary = uiState.taskSummary)
            }
        }

        // 4. Upcoming Tasks
        item {
            BinderCard(
                title         = "UPCOMING TASKS",
                actionLabel   = if (uiState.upcomingTasks.isNotEmpty()) "View All" else null,
                onActionClick = onNavigateToTasks
            ) {
                UpcomingTasksContent(tasks = uiState.upcomingTasks)
            }
        }
    }
}

// ─── Greeting ────────────────────────────────────────────────────────────────

@Composable
private fun GreetingCard(classCount: Int, taskCount: Int) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }

    val parts = mutableListOf<String>()
    if (classCount > 0) parts.add("$classCount ${if (classCount == 1) "class" else "classes"}")
    if (taskCount  > 0) parts.add("$taskCount ${if (taskCount  == 1) "task"  else "tasks"}")
    val subtitle = if (parts.isEmpty())
        "Nothing scheduled for today — enjoy your break!"
    else
        "You have ${parts.joinToString(" and ")} today."

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Text(
            text       = greeting,
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize   = 28.sp,
            color      = TextPrimary,
            lineHeight = 34.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// ─── Card Shell ───────────────────────────────────────────────────────────────

@Composable
private fun BinderCard(
    title: String,
    actionLabel: String?,
    onActionClick: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickMod: Modifier = if (onCardClick != null)
        Modifier.clickable(onClick = onCardClick)
    else
        Modifier

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 2.dp,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = Color(0x14000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .then(clickMod)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = TextPrimary
                )
                if (actionLabel != null) {
                    TextButton(
                        onClick        = onActionClick,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text       = actionLabel,
                            style      = MaterialTheme.typography.labelMedium,
                            color      = AppBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ─── Task Summary ─────────────────────────────────────────────────────────────

@Composable
private fun TaskSummaryContent(summary: TaskSummary) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BinderStatChip("Total",   summary.total,     AppBlue)
        BinderStatChip("Done",    summary.completed, GreenOk)
        BinderStatChip("Pending", summary.pending,   Color(0xFFE67E22))
    }
}

@Composable
private fun BinderStatChip(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text       = value.toString(),
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 26.sp,
            color      = color
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

// ─── Upcoming Tasks ───────────────────────────────────────────────────────────

@Composable
private fun UpcomingTasksContent(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        BinderEmptyState(Icons.Default.CheckCircle, "No upcoming tasks — you're all clear!")
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            tasks.forEachIndexed { index, task ->
                UpcomingTaskRow(task)
                if (index < tasks.lastIndex) {
                    HorizontalDivider(
                        color     = DividerColor,
                        thickness = 1.dp,
                        modifier  = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingTaskRow(task: Task) {
    val dateLabel = task.dueDate?.let { millis ->
        val daysLeft = ((millis - System.currentTimeMillis()) / 86_400_000L).toInt()
        when {
            daysLeft == 0 -> "Due Today"
            daysLeft == 1 -> "Due in 1 day"
            daysLeft > 1  -> "Due in $daysLeft days"
            else          -> "Overdue"
        }
    } ?: "No date"

    val typeColor = when (task.taskType) {
        TaskType.GENERAL      -> Color(0xFF9B59B6)
        TaskType.ASSIGNMENT   -> Color(0xFF3498DB)
        TaskType.LAB_WORK     -> GreenOk
        TaskType.MINI_PROJECT -> Color(0xFFE67E22)
    }
    val typeLabel = when (task.taskType) {
        TaskType.GENERAL      -> "General"
        TaskType.ASSIGNMENT   -> "Assignment"
        TaskType.LAB_WORK     -> "Lab Work"
        TaskType.MINI_PROJECT -> "Mini Project"
    }
    val taskIcon = when (task.taskType) {
        TaskType.ASSIGNMENT   -> Icons.Default.Description
        TaskType.LAB_WORK     -> Icons.Default.Science
        TaskType.MINI_PROJECT -> Icons.Default.Folder
        TaskType.GENERAL      -> Icons.Default.Task
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(typeColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = taskIcon,
                contentDescription = null,
                tint               = typeColor,
                modifier           = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text       = task.title,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text     = typeLabel,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text  = "·",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text     = dateLabel,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        if (task.dueDate != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PendingPillBg
            ) {
                Text(
                    text       = "PENDING",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = PendingPillFg,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 10.sp,
                    modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ─── Attendance ───────────────────────────────────────────────────────────────

@Composable
private fun AttendanceSummaryContent(summary: AttendanceDashboardSummary) {
    if (!summary.hasData) {
        BinderEmptyState(Icons.Default.BarChart, "No attendance data yet")
        return
    }

    if (summary.overallPercentage >= 75f) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text       = "%.0f%%".format(summary.overallPercentage),
                style      = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = GreenOk
            )
            Text(
                text       = "✓ All subjects above 75%",
                style      = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color      = GreenOk
            )
        }
        return
    }

    // Identical formula to AttendanceScreen.SubjectAttendanceSummary.classesNeeded():
    //   ceil((0.75 * total - present) / 0.25)
    // totalPresent and totalClasses are now exposed on AttendanceDashboardSummary.
    val needed: Int = if (summary.totalClasses > 0) {
        val raw = (0.75f * summary.totalClasses.toFloat() - summary.totalPresent.toFloat()) / 0.25f
        max(0, ceil(raw).toInt())
    } else 0
    val classWord = if (needed == 1) "class" else "classes"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AlertRedBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Warning,
                contentDescription = null,
                tint               = AlertRed,
                modifier           = Modifier.size(16.dp)
            )
            Text(
                text          = "ATTENDANCE ALERT",
                style         = MaterialTheme.typography.labelSmall,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color         = AlertRed,
                fontSize      = 10.sp
            )
        }

        Text(
            text       = "Critical Attendance",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color      = TextPrimary
        )

        if (summary.subjectsBelowThreshold > 0) {
            val s = summary.subjectsBelowThreshold
            Text(
                text     = "$s subject${if (s > 1) "s" else ""} below 75% requires immediate attention.",
                style    = MaterialTheme.typography.bodySmall,
                color    = TextSecondary,
                fontSize = 13.sp
            )
        }

        if (needed > 0) {
            Text(
                text       = "Attend $needed more $classWord to reach above 75%.",
                style      = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color      = AlertRed.copy(alpha = 0.85f),
                fontSize   = 13.sp
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text       = "%.0f%%".format(summary.overallPercentage),
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 34.sp,
                color      = AlertRed
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text     = "Below 75%",
                style    = MaterialTheme.typography.bodySmall,
                color    = AlertRed,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

// ─── Next Class ───────────────────────────────────────────────────────────────

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
                    tint               = AppBlue.copy(alpha = 0.3f),
                    modifier           = Modifier.size(36.dp)
                )
                Text(
                    text  = "No timetable yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                OutlinedButton(
                    onClick = onNavigateToSchedule,
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = AppBlue),
                    shape   = RoundedCornerShape(10.dp)
                ) {
                    Text("Set up timetable", fontWeight = FontWeight.Bold)
                }
            }
        }

        nextClass == null -> {
            BinderEmptyState(Icons.Default.Schedule, "No upcoming classes scheduled")
        }

        else -> {
            val days   = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val dayStr = days.getOrElse(nextClass.dayOfWeek - 1) { "" }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = dayStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Icon(
                    imageVector        = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint               = TextSecondary,
                    modifier           = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            NextClassSlotRow(nextClass)
        }
    }
}

private fun minutesToTimeStr(totalMinutes: Int): String {
    val h   = totalMinutes / 60
    val m   = totalMinutes % 60
    val ap  = if (h < 12) "AM" else "PM"
    val h12 = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else   -> h
    }
    return "%d:%02d %s".format(h12, m, ap)
}

@Composable
private fun NextClassSlotRow(slot: ClassSlot) {
    val startStr = minutesToTimeStr(slot.startTime)
    val endStr   = minutesToTimeStr(slot.endTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier            = Modifier.width(84.dp)
        ) {
            Text(
                text       = startStr,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                fontSize   = 13.sp
            )
            Text(
                text     = endStr,
                style    = MaterialTheme.typography.labelSmall,
                color    = TextSecondary,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .width(2.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(AppBlue.copy(alpha = 0.25f))
        )

        Column(Modifier.weight(1f)) {
            Text(
                text       = slot.subject,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
                fontSize   = 14.sp
            )
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint               = TextSecondary,
                    modifier           = Modifier.size(12.dp)
                )
                Text(
                    text  = slot.room,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

// ─── Shared Helpers ───────────────────────────────────────────────────────────

@Composable
private fun BinderEmptyState(icon: ImageVector, message: String) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = AppBlue.copy(alpha = 0.3f),
            modifier           = Modifier.size(32.dp)
        )
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}