package com.example.collegecompanion.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddTaskScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDCDDEA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔝 HEADER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF7B61FF))
        ) {
            Text(
                text = "ADD TASK",
                modifier = Modifier.padding(20.dp),
                color = Color.White, // ✅ FIXED (was Blue)
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 📋 BODY TEXT
        Text(
            text = "FORM COMES FROM NEXT SCREEN",
            color = Color(0xFF333333), // ✅ better readability
            style = MaterialTheme.typography.bodyLarge
        )
    }
}