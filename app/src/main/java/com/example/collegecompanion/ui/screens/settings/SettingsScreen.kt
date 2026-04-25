package com.example.collegecompanion.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import com.example.collegecompanion.notification.NotificationWorker
import androidx.hilt.navigation.compose.hiltViewModel

private val PageBg      = Color(0xFFF3F0FF)
private val CardPurple  = Color(0xFFDDD5FB)
private val CardGreen   = Color(0xFFCCF0DC)
private val CardPeach   = Color(0xFFFADDCC)
private val CardBlue    = Color(0xFFCCDEFA)
private val CardYellow  = Color(0xFFFAEECC)

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit, // Callback for navigation
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Button(onClick = {
        val request = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }) {
        Text("Test Notification Now")
    }

    // Listen for the sign-out event from ViewModel
    LaunchedEffect(Unit) {
        viewModel.signOutEvent.collect {
            onSignOut()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 52.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1340)
            )

            Spacer(Modifier.height(2.dp))

            // --- Profile ---
            PastelCard(color = CardPurple) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7B5CF0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Your Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D1060))
                        Text(uiState.userEmail ?: "Signed in with Google", fontSize = 13.sp, color = Color(0xFF6B3FA0))
                    }
                }
            }

            // --- Notifications ---
            PastelCard(color = CardGreen) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF27AE60)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Notifications", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D3B24))
                    }
                    HorizontalDivider(color = Color(0x330D3B24), thickness = 0.5.dp)

                    Text("Reminder time", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A6B3A))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Morning" to 8, "Afternoon" to 13, "Evening" to 18).forEach { (label, hour) ->
                            FilterChip(
                                selected = uiState.hourOfDay == hour,
                                onClick = { viewModel.setNotificationHour(hour) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF27AE60), selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }

            // --- Attendance ---
            PastelCard(color = CardPeach) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Attendance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C1A00))
                        Text("75% threshold warning", fontSize = 13.sp, color = Color(0xFF9C4020))
                    }
                    Box(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFD85A30)).padding(horizontal = 16.dp, vertical = 6.dp)) {
                        Text("Active", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // --- About ---
            PastelCard(color = CardBlue) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("About", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A2A5C))
                    InfoRow("App", "CollegeCompanion", Color(0xFF0A2A5C))
                    InfoRow("Version", "1.0.0", Color(0xFF0A2A5C))
                }
            }

            // --- Sign Out ---
            PastelCard(color = CardYellow) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A2800))
                        Text("You'll need to sign in again", fontSize = 12.sp, color = Color(0xFF7A4A00))
                    }
                    IconButton(
                        onClick = { viewModel.signOut() },
                        modifier = Modifier.clip(CircleShape).background(Color(0xFFE09000))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Sign out", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PastelCard(color: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun InfoRow(label: String, value: String, textColor: Color) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = textColor.copy(alpha = 0.6f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}