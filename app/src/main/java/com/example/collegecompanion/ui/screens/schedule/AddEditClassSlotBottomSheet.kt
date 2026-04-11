package com.example.collegecompanion.ui.screens.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.ClassType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassSlotBottomSheet(
    existingSlot: ClassSlot?,
    selectedDay: Int,
    onDismiss: () -> Unit,
    onSave: (ClassSlot) -> Unit
) {
    var subject   by remember { mutableStateOf(existingSlot?.subject   ?: "") }
    var room      by remember { mutableStateOf(existingSlot?.room      ?: "") }
    var professor by remember { mutableStateOf(existingSlot?.professor ?: "") }
    var classType by remember { mutableStateOf(existingSlot?.classType ?: ClassType.LECTURE) }
    var startHour by remember { mutableIntStateOf(existingSlot?.startTime?.div(60) ?: 9) }
    var startMin  by remember { mutableIntStateOf(existingSlot?.startTime?.rem(60) ?: 0) }
    var endHour   by remember { mutableIntStateOf(existingSlot?.endTime?.div(60)   ?: 10) }
    var endMin    by remember { mutableIntStateOf(existingSlot?.endTime?.rem(60)   ?: 0) }

    val isValid = subject.isNotBlank() && room.isNotBlank()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (existingSlot == null) "Add Class" else "Edit Class",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = room,
                onValueChange = { room = it },
                label = { Text("Room *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = professor,
                onValueChange = { professor = it },
                label = { Text("Professor (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Class Type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClassType.entries.forEach { type ->
                    FilterChip(
                        selected = classType == type,
                        onClick = { classType = type },
                        label = {
                            Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    )
                }
            }

            Text("Start Time", style = MaterialTheme.typography.labelLarge)
            TimePickerRow(
                hour = startHour, minute = startMin,
                onHourChange = { startHour = it }, onMinuteChange = { startMin = it }
            )

            Text("End Time", style = MaterialTheme.typography.labelLarge)
            TimePickerRow(
                hour = endHour, minute = endMin,
                onHourChange = { endHour = it }, onMinuteChange = { endMin = it }
            )

            Button(
                onClick = {
                    onSave(
                        ClassSlot(
                            id        = existingSlot?.id ?: 0,
                            dayOfWeek = existingSlot?.dayOfWeek ?: selectedDay,
                            startTime = startHour * 60 + startMin,
                            endTime   = endHour   * 60 + endMin,
                            subject   = subject.trim(),
                            room      = room.trim(),
                            professor = professor.trim().ifBlank { null },
                            classType = classType
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existingSlot == null) "Add Class" else "Save Changes")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerRow(
    hour: Int, minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        var hourExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = hourExpanded,
            onExpandedChange = { hourExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = formatHour(hour),
                onValueChange = {},
                readOnly = true,
                label = { Text("Hour") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = hourExpanded,
                onDismissRequest = { hourExpanded = false }
            ) {
                (6..21).forEach { h ->
                    DropdownMenuItem(
                        text = { Text(formatHour(h)) },
                        onClick = { onHourChange(h); hourExpanded = false }
                    )
                }
            }
        }

        var minExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = minExpanded,
            onExpandedChange = { minExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = "%02d".format(minute),
                onValueChange = {},
                readOnly = true,
                label = { Text("Min") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = minExpanded,
                onDismissRequest = { minExpanded = false }
            ) {
                listOf(0, 15, 30, 45).forEach { m ->
                    DropdownMenuItem(
                        text = { Text("%02d".format(m)) },
                        onClick = { onMinuteChange(m); minExpanded = false }
                    )
                }
            }
        }
    }
}

private fun formatHour(h: Int): String {
    val amPm = if (h < 12) "AM" else "PM"
    val display = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
    return "$display $amPm"
}