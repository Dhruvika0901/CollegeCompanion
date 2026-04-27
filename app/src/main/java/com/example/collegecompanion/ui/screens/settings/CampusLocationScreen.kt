//// ui/screens/settings/CampusLocationScreen.kt
//package com.example.collegecompanion.ui.screens.settings
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.LocationOn
//import androidx.compose.material.icons.filled.MyLocation
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//
//private val Indigo = Color(0xFF3D5AFE)
//private val PageBg = Color(0xFFF5F6FA)
//private val CardWhite = Color(0xFFFFFFFF)
//private val TextPrimary = Color(0xFF1A1F36)
//private val TextSub = Color(0xFF8A94A6)
//private val ErrorRed = Color(0xFFE53935)
//private val GreenOk = Color(0xFF2E7D32)
//
//@Composable
//fun CampusLocationScreen(
//    viewModel: CampusLocationViewModel = hiltViewModel()
//) {
//    val ui by viewModel.uiState.collectAsState()
//
//    // Success snackbar auto-dismiss
//    LaunchedEffect(ui.saveSuccess) {
//        if (ui.saveSuccess) {
//            kotlinx.coroutines.delay(2000)
//            viewModel.dismissSuccess()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(PageBg)
//            .padding(20.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        // Title
//        Text("Campus Location", fontSize = 26.sp,
//            fontWeight = FontWeight.ExtraBold, color = TextPrimary)
//        Text("Set your college's GPS boundary for auto attendance prompts.",
//            fontSize = 14.sp, color = TextSub)
//
//        // Current saved location banner
//        if (ui.saved != null) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(Color(0xFFE8F5E9))
//                    .padding(14.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                Icon(Icons.Default.Check, contentDescription = null,
//                    tint = GreenOk, modifier = Modifier.size(20.dp))
//                Column {
//                    Text("Campus location is set", fontSize = 13.sp,
//                        fontWeight = FontWeight.SemiBold, color = GreenOk)
//                    Text("Lat: ${"%.5f".format(ui.saved!!.latitude)}  " +
//                            "Lng: ${"%.5f".format(ui.saved!!.longitude)}  " +
//                            "Radius: ${ui.saved!!.radiusMeters.toInt()}m",
//                        fontSize = 11.sp, color = GreenOk.copy(alpha = 0.75f))
//                }
//            }
//        }
//
//        // Card: Use current GPS
//        Card(
//            shape  = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = CardWhite),
//            elevation = CardDefaults.cardElevation(2.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                Text("Use Current Location", fontSize = 15.sp,
//                    fontWeight = FontWeight.Bold, color = TextPrimary)
//                Text("Stand inside your college campus, then tap the button below.",
//                    fontSize = 13.sp, color = TextSub)
//                Button(
//                    onClick  = { viewModel.useCurrentLocation() },
//                    enabled  = !ui.isFetchingGps,
//                    shape    = RoundedCornerShape(12.dp),
//                    colors   = ButtonDefaults.buttonColors(containerColor = Indigo),
//                    modifier = Modifier.fillMaxWidth().height(48.dp)
//                ) {
//                    if (ui.isFetchingGps) {
//                        CircularProgressIndicator(color = Color.White,
//                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
//                    } else {
//                        Icon(Icons.Default.MyLocation, contentDescription = null,
//                            modifier = Modifier.size(18.dp))
//                        Spacer(Modifier.width(8.dp))
//                        Text("Set to My Current Location", fontWeight = FontWeight.SemiBold)
//                    }
//                }
//                // Pre-fills the fields below after GPS fetch
//                if (ui.latInput.isNotBlank() && ui.lngInput.isNotBlank()) {
//                    Text("Fetched → Lat: ${ui.latInput}  Lng: ${ui.lngInput}",
//                        fontSize = 11.sp, color = Indigo)
//                }
//            }
//        }
//
//        // Divider with OR
//        Row(verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
//            Text("OR", fontSize = 12.sp, color = TextSub)
//            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
//        }
//
//        // Card: Manual entry
//        Card(
//            shape  = RoundedCornerShape(16.dp),
//            colors = CardDefaults.cardColors(containerColor = CardWhite),
//            elevation = CardDefaults.cardElevation(2.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                Text("Enter Coordinates Manually", fontSize = 15.sp,
//                    fontWeight = FontWeight.Bold, color = TextPrimary)
//                OutlinedTextField(
//                    value         = ui.latInput,
//                    onValueChange = viewModel::onLatChange,
//                    label         = { Text("Latitude") },
//                    placeholder   = { Text("e.g. 19.075983") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    modifier      = Modifier.fillMaxWidth(),
//                    shape         = RoundedCornerShape(12.dp),
//                    singleLine    = true
//                )
//                OutlinedTextField(
//                    value         = ui.lngInput,
//                    onValueChange = viewModel::onLngChange,
//                    label         = { Text("Longitude") },
//                    placeholder   = { Text("e.g. 72.877655") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
//                    modifier      = Modifier.fillMaxWidth(),
//                    shape         = RoundedCornerShape(12.dp),
//                    singleLine    = true
//                )
//                OutlinedTextField(
//                    value         = ui.radiusInput,
//                    onValueChange = viewModel::onRadiusChange,
//                    label         = { Text("Geofence Radius (meters)") },
//                    placeholder   = { Text("200") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    modifier      = Modifier.fillMaxWidth(),
//                    shape         = RoundedCornerShape(12.dp),
//                    singleLine    = true,
//                    supportingText = { Text("Recommended: 100–300m", color = TextSub, fontSize = 11.sp) }
//                )
//            }
//        }
//
//        // Error message
//        if (ui.error != null) {
//            Text(ui.error!!, fontSize = 13.sp, color = ErrorRed, fontWeight = FontWeight.Medium)
//        }
//
//        // Success message
//        if (ui.saveSuccess) {
//            Row(verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                Icon(Icons.Default.Check, contentDescription = null,
//                    tint = GreenOk, modifier = Modifier.size(16.dp))
//                Text("Campus location saved! Geofence is active.",
//                    fontSize = 13.sp, color = GreenOk, fontWeight = FontWeight.SemiBold)
//            }
//        }
//
//        // Save button
//        Button(
//            onClick  = { viewModel.saveManual() },
//            shape    = RoundedCornerShape(12.dp),
//            colors   = ButtonDefaults.buttonColors(containerColor = Indigo),
//            modifier = Modifier.fillMaxWidth().height(52.dp)
//        ) {
//            Icon(Icons.Default.LocationOn, contentDescription = null,
//                modifier = Modifier.size(18.dp))
//            Spacer(Modifier.width(8.dp))
//            Text("Save Campus Location", fontWeight = FontWeight.Bold, fontSize = 15.sp)
//        }
//
//        // Clear button (only if something is saved)
//        if (ui.saved != null) {
//            OutlinedButton(
//                onClick  = { viewModel.clearLocation() },
//                shape    = RoundedCornerShape(12.dp),
//                colors   = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
//                border   = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
//                modifier = Modifier.fillMaxWidth().height(48.dp)
//            ) {
//                Text("Remove Campus Location", fontWeight = FontWeight.SemiBold)
//            }
//        }
//    }
//}