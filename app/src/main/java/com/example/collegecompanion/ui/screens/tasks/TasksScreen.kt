package com.example.collegecompanion.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.ui.tasks.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

/* ── Colour tokens ───────────────────────────────────────────── */
private val BgPage        = Color(0xFFF5F6FA)
private val AccentPurple  = Color(0xFF4B4ACF)
private val ChipUrgent    = Color(0xFFFF4D4F)
private val ChipProgress  = Color(0xFF4B4ACF)
private val ChipMini      = Color(0xFF7B6CF6)
private val ChipPending   = Color(0xFFFFA726)
private val CardWhite     = Color.White
private val TextPrimary   = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF9095A1)
private val Divider       = Color(0xFFEEEFF4)

/* ── Filter tabs ─────────────────────────────────────────────── */
private val filterTabs = listOf("All", "Assignments", "Lab Work", "Mini Projects")

@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredTasks = when (selectedFilter) {
        "Assignments"   -> tasks.filter { it.taskType == TaskType.ASSIGNMENT }
        "Lab Work"      -> tasks.filter { it.taskType == TaskType.LAB_WORK }
        "Mini Projects" -> tasks.filter { it.taskType == TaskType.MINI_PROJECT }
        else            -> tasks
    }

    Scaffold(
        containerColor = BgPage,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_task") },
                containerColor = AccentPurple,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task", tint = Color.White)
            }
        },
        bottomBar = { AgendaBottomBar() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            /* ── Page title ─────────────────────── */
            Text(
                text = "Your Agenda",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = TextPrimary,
                modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
            )

            /* ── Filter chips ───────────────────── */
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(filterTabs) { tab ->
                    val active = tab == selectedFilter
                    Surface(
                        onClick = { selectedFilter = tab },
                        shape = RoundedCornerShape(50),
                        color = if (active) AccentPurple else CardWhite,
                        shadowElevation = if (active) 0.dp else 1.dp
                    ) {
                        Text(
                            text = tab,
                            color = if (active) Color.White else TextSecondary,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            /* ── Task list ──────────────────────── */
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredTasks) { task ->
                    AgendaTaskCard(task)
                }
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Task Card
   ════════════════════════════════════════════════════════════════ */
@Composable
fun AgendaTaskCard(task: Task) {
    val isMiniProject = task.taskType == TaskType.MINI_PROJECT

    if (isMiniProject) {
        MiniProjectCard(task)
    } else {
        StandardTaskCard(task)
    }
}

/* ── Standard card (Assignment / Lab Work / Pending) ─────────── */
@Composable
fun StandardTaskCard(task: Task) {
    val (chipLabel, chipColor) = taskChipStyle(task)
    val dateText = task.dueDate?.let {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            /* Top row: chip + overflow */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(chipLabel, chipColor)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.MoreHoriz,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            /* Left accent bar + title */
            Row(verticalAlignment = Alignment.Top) {
                if (task.taskType == TaskType.ASSIGNMENT) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(46.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ChipUrgent)
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    task.subject?.let {
                        Text(
                            text = "Course: $it",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Divider(color = Divider, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))

            /* Bottom: date + avatar row */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                dateText?.let {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(it, fontSize = 12.sp, color = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                /* Stacked avatar placeholders */
                AvatarStack()
            }
        }
    }
}

/* ── Mini Project card (dark purple) ────────────────────────── */
@Composable
fun MiniProjectCard(task: Task) {
    val subTaskCount = 5 // placeholder; wire to real data if available

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AccentPurple),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            /* Top: chip + star */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip("MINI PROJECT", ChipMini, textColor = Color.White, outlined = true)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = task.title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color.White,
                lineHeight = 28.sp
            )

            task.subject?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(20.dp))

            /* Bottom: view brief button + subtask count */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White
                ) {
                    Text(
                        "VIEW BRIEF",
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Text(
                    "$subTaskCount Sub-tasks remaining",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Helpers
   ════════════════════════════════════════════════════════════════ */

@Composable
fun StatusChip(
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

@Composable
fun AvatarStack() {
    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        repeat(2) { i ->
            val colors = listOf(Color(0xFF7B6CF6), Color(0xFF4ECDC4))
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(colors[i % colors.size])
            )
        }
    }
}

private fun taskChipStyle(task: Task): Pair<String, Color> = when {
    task.taskType == TaskType.ASSIGNMENT -> "URGENT" to ChipUrgent
    task.taskType == TaskType.LAB_WORK   -> "IN PROGRESS" to ChipProgress
    else                                  -> "PENDING" to ChipPending
}

/* ════════════════════════════════════════════════════════════════
   Bottom Navigation Bar
   ════════════════════════════════════════════════════════════════ */
@Composable
fun AgendaBottomBar() {
    Surface(
        shadowElevation = 12.dp,
        color = CardWhite
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", tint = TextSecondary) }, label = "DASHBOARD")
            BottomNavItem(icon = { Icon(Icons.Outlined.Assignment, contentDescription = "Tasks", tint = AccentPurple) }, label = "TASKS", active = true)
            BottomNavItem(icon = { Icon(Icons.Outlined.Description, contentDescription = "Notes", tint = TextSecondary) }, label = "NOTES")
            BottomNavItem(icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = TextSecondary) }, label = "PROFILE")
        }
    }
}

@Composable
fun BottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    active: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        icon()
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) AccentPurple else TextSecondary
        )
    }
}