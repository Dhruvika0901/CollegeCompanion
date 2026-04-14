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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.collegecompanion.domain.model.Task
import com.example.collegecompanion.domain.model.TaskType
import com.example.collegecompanion.ui.tasks.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {

        // 🔥 TOP HEADER
        HeaderSection(tasks)

        // 📅 DATE STRIP
        DateStrip()

        // 📋 TASK TIMELINE
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tasks) { task ->
                TimelineTaskCard(task)
            }
        }

        // ➕ FLOAT BUTTON
        FloatingActionButton(
            onClick = { navController.navigate("add_task") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = Color(0xFF7B61FF)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
fun HeaderSection(tasks: List<Task>) {
    val completed = tasks.count { it.isCompleted }
    val pending = tasks.size - completed

    Card(
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7B61FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "YOUR WEEKLY PLAN",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryBox("COMPLETED", completed, Color(0xFFFFC107))
                SummaryBox("IN PROGRESS", pending, Color(0xFFFF6B6B))
                SummaryBox("OVERDUE", 0, Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun SummaryBox(title: String, count: Int, color: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun DateStrip() {
    val days = listOf("MON", "TUE", "WED", "THU", "FRI")

    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(days) { day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                Text(day, fontWeight = FontWeight.Bold)
                Text("23") // static for now
            }
        }
    }
}

@Composable
fun TimelineTaskCard(task: Task) {

    val color = when (task.taskType) {
        TaskType.ASSIGNMENT -> Color(0xFFFFCDD2)
        TaskType.LAB_WORK -> Color(0xFFBBDEFB)
        TaskType.MINI_PROJECT -> Color(0xFFC8E6C9)
        else -> Color.LightGray
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        // ⏱ TIME
        val time = task.dueDate?.let {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it))
        } ?: "NO TIME"

        Text(
            text = time.uppercase(),
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.labelMedium
        )

        // 📍 DOT + LINE
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Gray, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(80.dp)
                    .background(Color.LightGray)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 🧾 TASK CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    task.title.uppercase(),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                task.subject?.let {
                    Text(it.uppercase())
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    task.taskType.name.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}