package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.TaskType
import java.text.SimpleDateFormat
import java.util.*

private val Purple = Color(0xFF7B61FF)
private val LightBg = Color(0xFFF6F7FB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.dueDate ?: System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = LightBg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {
                    Text(if (viewModel.isEditing) "Edit Task" else "Add Task")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .background(LightBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Title
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.title = it
                    viewModel.titleError = false
                },
                label = { Text("Title *") },
                isError = viewModel.titleError,
                supportingText = if (viewModel.titleError) {
                    { Text("Title cannot be empty") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    cursorColor = Purple
                )
            )

            // ✅ Subject (MOVED HERE)
            OutlinedTextField(
                value = viewModel.subject,
                onValueChange = { viewModel.subject = it },
                label = { Text("Subject (optional)") },
                placeholder = { Text("e.g. DSA, OS Lab, DBMS…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    cursorColor = Purple
                )
            )

            // Description
            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description") },
                placeholder = { Text("Add notes, links, references…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    cursorColor = Purple
                )
            )

            // Due Date
            OutlinedTextField(
                value = viewModel.dueDate?.let {
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(Date(it))
                } ?: "",
                onValueChange = {},
                label = { Text("Due Date") },
                placeholder = { Text("Not set") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple
                )
            )

            // Task Type
            Text(
                "Task Type",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskType.entries.forEach { type ->

                    val label = when (type) {
                        TaskType.GENERAL -> "General"
                        TaskType.ASSIGNMENT -> "Assignment"
                        TaskType.LAB_WORK -> "Lab Work"
                        TaskType.MINI_PROJECT -> "Mini Project"
                    }

                    FilterChip(
                        selected = viewModel.taskType == type,
                        onClick = { viewModel.taskType = type },
                        label = {
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        },
                        modifier = Modifier.wrapContentWidth(),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Button
            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(if (viewModel.isEditing) "Update Task" else "Save Task")
            }
        }
    }
}