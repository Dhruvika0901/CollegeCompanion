package com.example.collegecompanion.ui.screens.schedule

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.ClassType
import java.time.LocalDate

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
        topBar = { TopAppBar(title = { Text("Schedule") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                slotToEdit = null
                showBottomSheet = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add class")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            ScrollableTabRow(
                selectedTabIndex = uiState.selectedDay - 1,
                edgePadding = 16.dp
            ) {
                DAY_LABELS.forEachIndexed { index, label ->
                    val day = index + 1
                    Tab(
                        selected = uiState.selectedDay == day,
                        onClick = { viewModel.selectDay(day) },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (day == today) FontWeight.Bold else FontWeight.Normal
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
                            slot = slot,
                            onEdit = { slotToEdit = slot; showBottomSheet = true },
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
            selectedDay = uiState.selectedDay,
            onDismiss = { showBottomSheet = false },
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No classes today", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap + to add a class slot",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddClick) { Text("Add Class") }
    }
}

@Composable
fun ClassSlotCard(
    slot: ClassSlot,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val (containerColor, contentColor) = when (slot.classType) {
        ClassType.LECTURE  -> MaterialTheme.colorScheme.primaryContainer to
                MaterialTheme.colorScheme.onPrimaryContainer
        ClassType.LAB      -> MaterialTheme.colorScheme.tertiaryContainer to
                MaterialTheme.colorScheme.onTertiaryContainer
        ClassType.TUTORIAL -> MaterialTheme.colorScheme.secondaryContainer to
                MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatTime(slot.startTime), style = MaterialTheme.typography.labelLarge,  color = contentColor)
                Text("–",                        style = MaterialTheme.typography.labelSmall,  color = contentColor)
                Text(formatTime(slot.endTime),   style = MaterialTheme.typography.labelMedium, color = contentColor)
            }

            Spacer(Modifier.width(16.dp))
            HorizontalDivider(modifier = Modifier.width(1.dp).height(56.dp))
            Spacer(Modifier.width(16.dp))

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
    val h = minutes / 60
    val m = minutes % 60
    val amPm = if (h < 12) "AM" else "PM"
    val display = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
    return "%d:%02d %s".format(display, m, amPm)
}