package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.TaskType
import java.text.SimpleDateFormat
import java.util.*

/* ── Colour tokens ─────────────────────────────────────────────── */
private val BgPage        = Color(0xFFF0F1F8)
private val AccentNavy    = Color(0xFF2D2D8E)
private val AccentPurple  = Color(0xFF4B4ACF)
private val CardWhite     = Color.White
private val InputBg       = Color(0xFFEAEBF5)
private val LabelGray     = Color(0xFF9095A1)
private val TextPrimary   = Color(0xFF1A1A2E)
private val ChipActive    = Color(0xFF2D2D8E)
private val ChipInactive  = Color(0xFFEAEBF5)

/* ════════════════════════════════════════════════════════════════
   Screen
   ════════════════════════════════════════════════════════════════ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showCustomReminderDialog by remember { mutableStateOf(false) }
    var customDaysInput by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.dueDate ?: System.currentTimeMillis()
    )

    /* ── Date picker dialog ─────────────────────────── */
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK", color = AccentPurple) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = LabelGray)
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    /* ── Custom reminder dialog ─────────────────────── */
    if (showCustomReminderDialog) {
        AlertDialog(
            onDismissRequest = { showCustomReminderDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Custom Reminder", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                OutlinedTextField(
                    value = customDaysInput,
                    onValueChange = { customDaysInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Days before due date") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        cursorColor = AccentPurple
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val days = customDaysInput.toIntOrNull()
                    if (days != null && days > 0) viewModel.reminderDaysBefore = days
                    showCustomReminderDialog = false
                }) { Text("Set", color = AccentPurple, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCustomReminderDialog = false
                    viewModel.reminderDaysBefore = null
                }) { Text("Clear", color = LabelGray) }
            }
        )
    }

    Scaffold(
        containerColor = BgPage,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgPage,
                    navigationIconContentColor = AccentNavy
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AccentNavy
                        )
                    }
                },
                title = {
                    Text(
                        text = if (viewModel.isEditing) "Edit Task" else "Add New Task",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                },
                actions = {
                    /* Avatar placeholder */
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            /* ── Task Title ─────────────────────────── */
            FormSection(label = "TASK TITLE") {
                FlatInputField(
                    value = viewModel.title,
                    onValueChange = {
                        viewModel.title = it
                        viewModel.titleError = false
                    },
                    placeholder = "e.g. Thermodynamics Case Study",
                    isError = viewModel.titleError,
                    errorText = "Title cannot be empty"
                )
            }

            /* ── Subject ────────────────────────────── */
            FormSection(label = "SUBJECT") {
                DropdownInputField(
                    value = viewModel.subject,
                    onValueChange = { viewModel.subject = it },
                    placeholder = "Advanced Mathematics"
                )
            }

            /* ── Task Type ──────────────────────────── */
            FormSection(label = "TASK TYPE") {
                TaskTypeDropdown(
                    selected = viewModel.taskType,
                    onSelect = { viewModel.taskType = it }
                )
            }

            /* ── Deadline card ──────────────────────── */
            IconInfoCard(
                icon = Icons.Default.CalendarMonth,
                iconBg = Color(0xFFE8E8FF),
                iconTint = AccentPurple,
                title = "Deadline",
                subtitle = "When is this due?",
                trailingContent = {
                    Text(
                        text = viewModel.dueDate?.let {
                            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(it))
                        } ?: "mm/dd/yyyy",
                        color = LabelGray,
                        fontSize = 14.sp
                    )
                },
                onClick = { showDatePicker = true }
            )

            /* ── Enable Reminder toggle ─────────────── */
            val reminderEnabled = viewModel.dueDate != null
            IconInfoCard(
                icon = Icons.Default.Notifications,
                iconBg = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF8F00),
                title = "Enable Reminder",
                subtitle = null,
                trailingContent = {
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { /* toggling reminder visibility is driven by dueDate */ },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentPurple,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCCCCDD)
                        )
                    )
                },
                onClick = null
            )

            /* ── Reminder chips (visible when due date set) ── */
            if (viewModel.dueDate != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReminderChip(
                        label = "1 day before",
                        selected = viewModel.reminderDaysBefore == 1,
                        onClick = {
                            viewModel.reminderDaysBefore =
                                if (viewModel.reminderDaysBefore == 1) null else 1
                        }
                    )
                    ReminderChip(
                        label = "2 days before",
                        selected = viewModel.reminderDaysBefore == 2,
                        onClick = {
                            viewModel.reminderDaysBefore =
                                if (viewModel.reminderDaysBefore == 2) null else 2
                        }
                    )
                    ReminderChip(
                        label = if (viewModel.reminderDaysBefore != null &&
                            viewModel.reminderDaysBefore != 1 &&
                            viewModel.reminderDaysBefore != 2
                        ) "✏ ${viewModel.reminderDaysBefore}d before" else "✏ Custom",
                        selected = viewModel.reminderDaysBefore != null &&
                                viewModel.reminderDaysBefore != 1 &&
                                viewModel.reminderDaysBefore != 2,
                        onClick = { showCustomReminderDialog = true }
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(12.dp))

            /* ── Save button ────────────────────────── */
            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentNavy,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = if (viewModel.isEditing) "Update Task ✏" else "Create Task 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/* ════════════════════════════════════════════════════════════════
   Reusable components
   ════════════════════════════════════════════════════════════════ */

@Composable
private fun FormSection(label: String, content: @Composable () -> Unit) {
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
private fun FlatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorText: String? = null
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InputBg)
                .border(
                    width = if (isError) 1.dp else 0.dp,
                    color = if (isError) Color(0xFFE53935) else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            BasicInputField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder
            )
        }
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = Color(0xFFE53935),
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun BasicInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = LabelGray, fontSize = 14.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AccentPurple,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun DropdownInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InputBg)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BasicInputField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder
                )
            }
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = LabelGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTypeDropdown(
    selected: TaskType,
    onSelect: (TaskType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val label = when (selected) {
        TaskType.GENERAL      -> "General"
        TaskType.ASSIGNMENT   -> "Assignment"
        TaskType.LAB_WORK     -> "Lab Work"
        TaskType.MINI_PROJECT -> "Mini Project"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InputBg)
                .menuAnchor()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = LabelGray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = CardWhite
        ) {
            TaskType.entries.forEach { type ->
                val typeLabel = when (type) {
                    TaskType.GENERAL      -> "General"
                    TaskType.ASSIGNMENT   -> "Assignment"
                    TaskType.LAB_WORK     -> "Lab Work"
                    TaskType.MINI_PROJECT -> "Mini Project"
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            typeLabel,
                            color = if (type == selected) AccentPurple else TextPrimary,
                            fontWeight = if (type == selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun IconInfoCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String?,
    trailingContent: @Composable () -> Unit,
    onClick: (() -> Unit)?
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            /* Icon bubble */
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }

            /* Texts */
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                if (subtitle != null) {
                    Text(subtitle, fontSize = 12.sp, color = LabelGray)
                }
            }

            trailingContent()
        }
    }
}

@Composable
private fun ReminderChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) ChipActive else ChipInactive
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else LabelGray,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}