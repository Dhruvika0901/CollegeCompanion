package com.example.collegecompanion.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.ClassType
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import java.time.LocalDate

private val Purple = Color(0xFF7B61FF)
private val LightBg = Color(0xFFF6F7FB)

private val DAY_LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var slotToEdit by remember { mutableStateOf<ClassSlot?>(null) }
    val today = LocalDate.now().dayOfWeek.value.coerceIn(1, 6)

    Scaffold(
        containerColor = LightBg,

        topBar = {
            TopAppBar(
                title = { Text("Schedule") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple,
                    titleContentColor = Color.White
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    slotToEdit = null
                    showBottomSheet = true
                },
                containerColor = Purple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add class")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .background(LightBg)
        ) {

            ScrollableTabRow(
                selectedTabIndex = uiState.selectedDay - 1,
                edgePadding = 16.dp,
                containerColor = LightBg,
                contentColor = Purple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            tabPositions[uiState.selectedDay - 1]
                        ),
                        color = Purple
                    )
                }
            ) {
                DAY_LABELS.forEachIndexed { index, label ->
                    val day = index + 1
                    Tab(
                        selected = uiState.selectedDay == day,
                        onClick  = { viewModel.selectDay(day) },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (day == today) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.selectedDay == day) Purple else Color.Gray
                            )
                        }
                    )
                }
            }

            if (uiState.slots.isEmpty()) {
                EmptyScheduleState(onAddClick = {
                    slotToEdit = null
                    showBottomSheet = true
                })
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.slots, key = { it.id }) { slot ->
                        ClassSlotCard(
                            slot     = slot,
                            onEdit   = { slotToEdit = slot; showBottomSheet = true },
                            onDelete = { viewModel.deleteSlot(slot) }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        AddEditClassSlotBottomSheet(
            existingSlot = slotToEdit,
            selectedDay  = uiState.selectedDay,
            onDismiss    = { showBottomSheet = false },
            onSave = { slot ->
                if (slotToEdit == null) viewModel.addSlot(slot)
                else viewModel.updateSlot(slot)
                showBottomSheet = false
            }
        )
    }
}

@Composable
private fun EmptyScheduleState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Text("No classes today", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap + to add a class slot",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White)
        ) {
            Text("Add Class")
        }
    }
}

@Composable
fun ClassSlotCard(
    slot: ClassSlot,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isBreak = slot.classType == ClassType.BREAK

    // 🎨 Custom colors (better than default Material)
    val (containerColor, contentColor) = when (slot.classType) {
        ClassType.LECTURE  -> Color(0xFFE6E0FF) to Color(0xFF3A2DB3)
        ClassType.LAB      -> Color(0xFFDFF7F0) to Color(0xFF1BA97F)
        ClassType.TUTORIAL -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        ClassType.BREAK    -> Color(0xFFF1F1F1) to Color.DarkGray
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatTime(slot.startTime), style = MaterialTheme.typography.labelLarge, color = contentColor)
                Text("–", color = contentColor)
                Text(formatTime(slot.endTime), style = MaterialTheme.typography.labelMedium, color = contentColor)
            }

            Spacer(Modifier.width(16.dp))
            HorizontalDivider(
                modifier = Modifier
                    .width(1.dp)
                    .height(56.dp),
                color = contentColor.copy(alpha = 0.3f)
            )
            Spacer(Modifier.width(16.dp))

            if (isBreak) {
                Text(
                    text = "☕ Break",
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = slot.subject,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "📍 ${slot.room}  •  ${slot.classType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor
                    )
                    if (!slot.professor.isNullOrBlank()) {
                        Text(
                            text = "👤 ${slot.professor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor
                        )
                    }
                }
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = contentColor)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = contentColor)
                }
            }
        }
    }
}

private fun formatTime(minutes: Int): String {
    val h       = minutes / 60
    val m       = minutes % 60
    val amPm    = if (h < 12) "AM" else "PM"
    val display = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
    return "%d:%02d %s".format(display, m, amPm)
}