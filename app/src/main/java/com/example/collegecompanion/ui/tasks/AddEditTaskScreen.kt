package com.example.collegecompanion.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "Edit task" else "New task") },
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
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value         = form.title,
                onValueChange = viewModel::onTitleChange,
                label         = { Text("Title") },
                isError       = form.titleError != null,
                supportingText = { form.titleError?.let { Text(it) } },
                modifier      = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value         = form.subject,
                onValueChange = viewModel::onSubjectChange,
                label         = { Text("Subject") },
                modifier      = Modifier.fillMaxWidth()
            )
            Text("Priority", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = form.priority == p,
                        onClick  = { viewModel.onPriorityChange(p) },
                        label    = { Text(p.name) }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick  = { viewModel.saveTask(onNavigateBack) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save task")
            }
        }
    }
}