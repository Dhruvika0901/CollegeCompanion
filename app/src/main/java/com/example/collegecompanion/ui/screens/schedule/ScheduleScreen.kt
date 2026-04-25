package com.example.collegecompanion.ui.screens.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.collegecompanion.domain.model.ClassSlot
import com.example.collegecompanion.domain.model.ClassType
import java.time.LocalDate
import java.time.LocalTime

// ─── Design tokens ────────────────────────────────────────────────────────────
private val Indigo       = Color(0xFF3D5AFE)
private val IndigoLight  = Color(0xFFEEF1FF)
private val PageBg       = Color(0xFFF5F6FA)
private val CardWhite    = Color(0xFFFFFFFF)
private val TextPrimary  = Color(0xFF1A1F36)
private val TextSub      = Color(0xFF8A94A6)
private val ChipBg       = Color(0xFFF0F1F5)
private val OngoingGreen = Color(0xFF00C48C)

// Text label shown in the type chip on each card
private fun classTypeLabel(type: ClassType): String = when (type) {
    ClassType.LECTURE  -> "Lecture"
    ClassType.LAB      -> "Lab"
    ClassType.TUTORIAL -> "Tutorial"
    ClassType.BREAK    -> "Break"
}

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var slotToEdit by remember { mutableStateOf<ClassSlot?>(null) }
    val todayInt = LocalDate.now().dayOfWeek.value.coerceIn(1, 6)

    Scaffold(
        containerColor = PageBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { slotToEdit = null; showBottomSheet = true },
                shape          = CircleShape,
                containerColor = Indigo,
                contentColor   = Color.White,
                modifier       = Modifier
                    .shadow(elevation = 12.dp, shape = CircleShape)
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add class")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // ── Top app bar ───────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector        = Icons.Default.Menu,
                        contentDescription = null,
                        tint               = Indigo,
                        modifier           = Modifier.size(22.dp)
                    )
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IndigoLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Person,
                            contentDescription = null,
                            tint               = Indigo,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ── Page header ───────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 20.dp)
                ) {
                    Text(
                        text          = "WEEKLY SCHEDULE",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        letterSpacing = 1.4.sp,
                        color         = TextSub
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "Your Timetable",
                        fontSize   = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary,
                        lineHeight = 36.sp
                    )
                }
            }

            // ── Day tab row ───────────────────────────────────────────
            item {
                val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                LazyRow(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dayLabels.size) { index ->
                        val day      = index + 1
                        val selected = uiState.selectedDay == day
                        val isToday  = day == todayInt

                        val bgColor by animateColorAsState(
                            targetValue   = if (selected) Indigo else Color.Transparent,
                            animationSpec = tween(200),
                            label         = "tabBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue   = if (selected) Color.White
                            else if (isToday) Indigo
                            else TextSub,
                            animationSpec = tween(200),
                            label         = "tabText"
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(bgColor)
                                .then(
                                    if (!selected) Modifier
                                        .clip(RoundedCornerShape(50))
                                    else Modifier
                                )
                                .let {
                                    if (!selected)
                                        it.background(
                                            color = if (isToday) IndigoLight else ChipBg,
                                            shape = RoundedCornerShape(50)
                                        )
                                    else it
                                }
                        ) {
                            TextButton(
                                onClick            = { viewModel.selectDay(day) },
                                contentPadding     = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                                shape              = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text       = dayLabels[index],
                                    fontSize   = 14.sp,
                                    fontWeight = if (selected || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color      = textColor
                                )
                            }
                        }
                    }
                }
            }

            // ── Spacer between header and cards ───────────────────────
            item { Spacer(Modifier.height(12.dp)) }

            // ── Empty state ───────────────────────────────────────────
            if (uiState.slots.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text       = "No classes today",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text  = "Tap + to add a class slot",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSub
                        )
                    }
                }
            } else {
                // ── Class cards ───────────────────────────────────────
                items(uiState.slots, key = { it.id }) { slot ->
                    val nowMinutes = LocalTime.now().let { it.hour * 60 + it.minute }
                    val isOngoing  = slot.dayOfWeek == LocalDate.now().dayOfWeek.value &&
                            nowMinutes >= slot.startTime &&
                            nowMinutes < slot.endTime
                    val minutesLeft = if (isOngoing) slot.endTime - nowMinutes else 0

                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        ClassSlotCard(
                            slot        = slot,
                            isOngoing   = isOngoing,
                            minutesLeft = minutesLeft,
                            onEdit      = { slotToEdit = slot; showBottomSheet = true },
                            onDelete    = { viewModel.deleteSlot(slot) }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        AddEditClassSlotBottomSheet(
            existingSlot = slotToEdit,
            selectedDay  = uiState.selectedDay,
            onDismiss    = { showBottomSheet = false },
            onSave = { slot ->
                if (slotToEdit == null) viewModel.addSlot(slot)
                else viewModel.updateSlot(slot)
                showBottomSheet = false
            }
        )
    }
}

// ─── Class slot card ──────────────────────────────────────────────────────────
@Composable
fun ClassSlotCard(
    slot: ClassSlot,
    isOngoing: Boolean   = false,
    minutesLeft: Int     = 0,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isBreak = slot.classType == ClassType.BREAK

    // Ongoing → dark indigo filled; others → white card
    val cardBg    = if (isOngoing) Indigo else CardWhite
    val textMain  = if (isOngoing) Color.White else TextPrimary
    val textDim   = if (isOngoing) Color.White.copy(alpha = 0.7f) else TextSub
    val chipBg    = if (isOngoing) Color.White.copy(alpha = 0.15f) else ChipBg
    val chipText  = if (isOngoing) Color.White else TextPrimary
    val iconTint  = if (isOngoing) Color.White.copy(alpha = 0.85f) else Indigo

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation    = if (isOngoing) 8.dp else 2.dp,
                shape        = RoundedCornerShape(20.dp),
                ambientColor = if (isOngoing) Indigo.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // ── Time range row ────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "${formatTime(slot.startTime)}  —  ${formatTime(slot.endTime)}",
                    fontSize = 12.sp,
                    color    = textDim,
                    fontWeight = FontWeight.Medium
                )
                if (isOngoing) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OngoingGreen)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text       = "ONGOING",
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Subject + type label chip ─────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = if (isBreak) "BREAK" else slot.subject.uppercase(),
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = textMain,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                // Class-type text chip (top-right)
                Box(
                    modifier         = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(chipBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = classTypeLabel(slot.classType),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (isOngoing) Color.White else Indigo
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ── Info chips row ────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                if (!isBreak) {
                    // Room chip — Material LocationOn icon instead of emoji
                    LocationInfoChip(
                        label = slot.room,
                        bg    = chipBg,
                        fg    = chipText,
                        tint  = if (isOngoing) Color.White else Indigo
                    )
                    // Professor chip
                    if (!slot.professor.isNullOrBlank()) {
                        InfoChip(
                            icon  = "👤",
                            label = slot.professor,
                            bg    = chipBg,
                            fg    = chipText
                        )
                    }
                }
                // Time-left chip (only when ongoing)
                if (isOngoing && minutesLeft > 0) {
                    InfoChip(
                        icon  = "⏱",
                        label = "${minutesLeft}m left",
                        bg    = Color.White.copy(alpha = 0.18f),
                        fg    = Color.White
                    )
                }
            }

            // ── Edit / Delete actions ─────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick  = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint               = iconTint,
                        modifier           = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint               = iconTint,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─── Location chip (uses Material icon instead of emoji) ─────────────────────
@Composable
private fun LocationInfoChip(label: String, bg: Color, fg: Color, tint: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector        = Icons.Default.LocationOn,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(13.dp)
        )
        Text(
            text       = label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = fg,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }
}

// ─── Info chip ────────────────────────────────────────────────────────────────
@Composable
private fun InfoChip(icon: String, label: String, bg: Color, fg: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 11.sp)
        Text(
            text       = label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            color      = fg,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }
}

// ─── Time formatter ───────────────────────────────────────────────────────────
private fun formatTime(minutes: Int): String {
    val h       = minutes / 60
    val m       = minutes % 60
    val amPm    = if (h < 12) "AM" else "PM"
    val display = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
    return "%d:%02d %s".format(display, m, amPm)
}