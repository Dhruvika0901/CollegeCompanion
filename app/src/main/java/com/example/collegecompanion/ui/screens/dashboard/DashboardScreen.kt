package com.example.collegecompanion.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.Priority
import com.example.collegecompanion.ui.tasks.TaskViewModel

@Composable
fun DashboardScreen(                          // ← NO navController here
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    val total     = tasks.size
    val completed = tasks.count { it.isCompleted }
    val pending   = total - completed
    val highPri   = tasks.count { it.priority == Priority.HIGH && !it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Total",     total.toString(),     Modifier.weight(1f))
            StatCard("Done",      completed.toString(), Modifier.weight(1f))
            StatCard("Pending",   pending.toString(),   Modifier.weight(1f))
            StatCard("High pri.", highPri.toString(),   Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
