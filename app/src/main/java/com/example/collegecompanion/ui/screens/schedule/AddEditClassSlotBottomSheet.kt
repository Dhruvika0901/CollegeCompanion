package com.example.collegecompanion.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.ClassType

/* ── Colour tokens ─────────────────────────────────────────────── */
private val BgSheet      = Color(0xFFF0F1F8)
private val AccentNavy   = Color(0xFF2D2D8E)
private val AccentPurple = Color(0xFF4B4ACF)
private val CardWhite    = Color.White
private val InputBg      = Color(0xFFEAEBF5)
private val LabelGray    = Color(0xFF9095A1)
private val TextPrimary  = Color(0xFF1A1A2E)
private val ChipActive   = Color(0xFF2D2D8E)
private val ChipInactive = Color(0xFFEAEBF5)
private val DividerColor = Color(0xFFE0E1EE)

/* ════════════════════════════════════════════════════════════════
   Bottom Sheet
   ════════════════════════════════════════════════════════════════ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassSlotBottomSheet(
    existingSlot: ClassSlot?,
    selectedDay: Int,
    onDismiss: () -> Unit,
    onSave: (ClassSlot) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    var subject   by remember { mutableStateOf(existingSlot?.subject ?: "") }
    var room      by remember { mutableStateOf(existingSlot?.room ?: "") }
    var professor by remember { mutableStateOf(existingSlot?.professor ?: "") }
    var classType by remember { mutableStateOf(existingSlot?.classType ?: ClassType.LECTURE) }

    var startHour by remember { mutableIntStateOf(existingSlot?.startTime?.div(60) ?: 9) }
    var startMin  by remember { mutableIntStateOf(existingSlot?.startTime?.rem(60) ?: 0) }
    var endHour   by remember { mutableIntStateOf(existingSlot?.endTime?.div(60) ?: 10) }
    var endMin    by remember { mutableIntStateOf(existingSlot?.endTime?.rem(60) ?: 0) }

    val isAddingNew = existingSlot == null

    LaunchedEffect(selectedDay, isAddingNew) {
        if (isAddingNew) {
            val lastSlot = viewModel.getLastSlotForDay(selectedDay)
            if (lastSlot != null) {
                startHour = lastSlot.endTime / 60
                startMin  = lastSlot.endTime % 60
                val rawEndHour = (lastSlot.endTime / 60) + 1
                endHour = rawEndHour.coerceAtMost(23)
                endMin  = lastSlot.endTime % 60
            }
        }
    }

    val isBreak = classType == ClassType.BREAK
    val isValid = isBreak || (subject.isNotBlank() && room.isNotBlank())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BgSheet,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 6.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(DividerColor)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            /* ── Header ───────────────────────────── */
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = if (existingSlot == null) "Add Class" else "Edit Class",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = TextPrimary
                )
                Text(
                    text = if (existingSlot == null) "Fill in the details below" else "Update class information",
                    fontSize = 13.sp,
                    color = LabelGray
                )
            }

            /* ── Class Type chips ─────────────────── */
            SheetSection(label = "CLASS TYPE") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClassType.entries.forEach { type ->
                        val label = when (type) {
                            ClassType.LECTURE  -> "Lecture"
                            ClassType.LAB      -> "Lab"
                            ClassType.TUTORIAL -> "Tutorial"
                            ClassType.BREAK    -> "Break"
                        }
                        val selected = classType == type
                        Surface(
                            onClick = { classType = type },
                            shape = RoundedCornerShape(50),
                            color = if (selected) ChipActive else ChipInactive
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else LabelGray,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            /* ── Subject / Room / Professor ─────────── */
            if (!isBreak) {
                SheetSection(label = "SUBJECT") {
                    FlatField(
                        value = subject,
                        onValueChange = { subject = it },
                        placeholder = "e.g. Advanced Mathematics"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SheetSection(label = "ROOM") {
                            IconFlatField(
                                value = room,
                                onValueChange = { room = it },
                                placeholder = "e.g. A-204",
                                icon = Icons.Default.MeetingRoom
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SheetSection(label = "PROFESSOR") {
                            IconFlatField(
                                value = professor,
                                onValueChange = { professor = it },
                                placeholder = "Optional",
                                icon = Icons.Default.Person
                            )
                        }
                    }
                }
            }

            /* ── Time card ────────────────────────── */
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE8E8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            "Time Slot",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }

                    HorizontalDivider(color = DividerColor)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "START TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LabelGray,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            TimePickerRow(
                                hour = startHour,
                                minute = startMin,
                                onHourChange = { h ->
                                    startHour = h
                                    if (endHour * 60 + endMin <= h * 60 + startMin) {
                                        endHour = (h + 1).coerceAtMost(23)
                                        endMin  = startMin
                                    }
                                },
                                onMinuteChange = { m -> startMin = m }
                            )
                        }

                        /* Divider */
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .align(Alignment.CenterVertically)
                                .background(DividerColor)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "END TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LabelGray,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            TimePickerRow(
                                hour = endHour,
                                minute = endMin,
                                onHourChange = { h -> endHour = h },
                                onMinuteChange = { m -> endMin = m }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            /* ── Save button ─────────────────────── */
            Button(
                onClick = {
                    onSave(
                        ClassSlot(
                            id        = existingSlot?.id ?: 0,
                            dayOfWeek = existingSlot?.dayOfWeek ?: selectedDay,
                            startTime = startHour * 60 + startMin,
                            endTime   = endHour * 60 + endMin,
                            subject   = if (isBreak) "Break" else subject.trim(),
                            room      = if (isBreak) "" else room.trim(),
                            professor = if (isBreak) null else professor.trim().ifBlank { null },
                            classType = classType
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentNavy,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFCCCCDD),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = if (existingSlot == null) "Add Class 📚" else "Save Changes ✓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Time picker — two flat dropdowns side by side
   ════════════════════════════════════════════════════════════════ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerRow(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        var hourExpanded by remember { mutableStateOf(false) }
        var minExpanded  by remember { mutableStateOf(false) }

        /* Hour */
        ExposedDropdownMenuBox(
            expanded = hourExpanded,
            onExpandedChange = { hourExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TimeDropdownField(
                value = formatHour(hour),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = hourExpanded,
                onDismissRequest = { hourExpanded = false },
                containerColor = CardWhite
            ) {
                (6..23).forEach { h ->
                    DropdownMenuItem(
                        text = { Text(formatHour(h), color = TextPrimary) },
                        onClick = { onHourChange(h); hourExpanded = false }
                    )
                }
            }
        }

        /* Minute */
        ExposedDropdownMenuBox(
            expanded = minExpanded,
            onExpandedChange = { minExpanded = it },
            modifier = Modifier.weight(1f)
        ) {
            TimeDropdownField(
                value = "%02d".format(minute),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = minExpanded,
                onDismissRequest = { minExpanded = false },
                containerColor = CardWhite
            ) {
                listOf(0, 15, 30, 45).forEach { m ->
                    DropdownMenuItem(
                        text = { Text("%02d".format(m), color = TextPrimary) },
                        onClick = { onMinuteChange(m); minExpanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeDropdownField(value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(InputBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = LabelGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

/* ════════════════════════════════════════════════════════════════
   Shared helpers
   ════════════════════════════════════════════════════════════════ */

@Composable
private fun SheetSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = LabelGray,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
private fun FlatField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InputBg)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = LabelGray, fontSize = 14.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AccentPurple,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun IconFlatField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InputBg)
            .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = LabelGray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = LabelGray, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AccentPurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }
    }
}

private fun formatHour(h: Int): String {
    val amPm = if (h < 12) "AM" else "PM"
    val display = when {
        h == 0 -> 12
        h > 12 -> h - 12
        else   -> h
    }
    return "$display $amPm"
}