package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.TaskType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onNavigateBack: () -> Unit,                  // ← matches CollegeCompanionApp exactly
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    // taskId comes in via SavedStateHandle inside the ViewModel
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
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Title
            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it; viewModel.titleError = false },
                label = { Text("Title *") },
                isError = viewModel.titleError,
                supportingText = if (viewModel.titleError) {
                    { Text("Title cannot be empty") }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                maxLines = 5
            )

            // Due date
            OutlinedTextField(
                value = viewModel.dueDate?.let {
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
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
                modifier = Modifier.fillMaxWidth()
            )

            // Task type
            Text(
                "Task Type",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskType.entries.forEach { type ->
                    val label = when (type) {
                        TaskType.GENERAL      -> "General"
                        TaskType.ASSIGNMENT   -> "Assignment"
                        TaskType.LAB_WORK     -> "Lab Work"
                        TaskType.MINI_PROJECT -> "Mini Project"
                    }
                    FilterChip(
                        selected = viewModel.taskType == type,
                        onClick  = { viewModel.taskType = type },
                        label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Subject
            OutlinedTextField(
                value = viewModel.subject,
                onValueChange = { viewModel.subject = it },
                label = { Text("Subject (optional)") },
                placeholder = { Text("e.g. DSA, OS Lab, DBMS…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { viewModel.save(onSuccess = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (viewModel.isEditing) "Update Task" else "Save Task")
            }
        }
    }
}