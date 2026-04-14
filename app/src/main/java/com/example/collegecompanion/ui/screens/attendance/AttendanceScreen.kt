package com.example.collegecompanion.ui.screens.attendance

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.collegecompanion.domain.model.AttendanceStatus
import java.time.LocalDate
import kotlin.math.ceil

// ── Pastel palette ────────────────────────────────────────────────────────────
private val CardPurple      = Color(0xFFD4C5F9)
private val CardPurpleDark  = Color(0xFF5B3FA0)
private val CardGreen       = Color(0xFFC5EDD6)
private val CardGreenDark   = Color(0xFF1B5E40)
private val CardPeach       = Color(0xFFF9D5C5)
private val CardPeachDark   = Color(0xFF8B3A1A)
private val CardBlue        = Color(0xFFC5D9F9)
private val CardBlueDark    = Color(0xFF0C2D6B)
private val CardRed         = Color(0xFFF9C5C5)
private val CardRedDark     = Color(0xFF8B1A1A)
private val PageBg          = Color(0xFFF5F3FF)

// ── Status helpers ────────────────────────────────────────────────────────────
private enum class StandingLabel(
    val text: String,
    val cardBg: Color,
    val cardDark: Color
) {
    CRITICAL     ("CRITICAL",     CardRed,    CardRedDark),
    ON_TRACK     ("ON TRACK",     CardBlue,   CardBlueDark),
    GOOD_STANDING("GOOD STANDING",CardGreen,  CardGreenDark)
}

private fun SubjectAttendanceSummary.standing(): StandingLabel = when {
    total == 0       -> StandingLabel.ON_TRACK
    percentage < 65f -> StandingLabel.CRITICAL
    percentage < 75f -> StandingLabel.ON_TRACK
    else             -> StandingLabel.GOOD_STANDING
}

private fun SubjectAttendanceSummary.classesNeeded(): Int {
    if (percentage >= 75f) return 0
    val x = ceil((0.75f * total - present) / 0.25f).toInt()
    return maxOf(0, x)
}

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: AttendanceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CardPurpleDark)
            }
            return@Box
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp, top = 16.dp, bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Top bar ───────────────────────────────────────────────
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CardPurpleDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Attendance",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(
                            text = "Track your class presence",
                            fontSize = 13.sp,
                            color = Color(0xFF6B6B8A)
                        )
                    }
                }
            }

            if (state.summaries.isEmpty()) {
                item { EmptyAttendanceState() }
                return@LazyColumn
            }

            // ── Overall banner ────────────────────────────────────────
            item { OverallBanner(summaries = state.summaries) }

            // ── Subject cards ─────────────────────────────────────────
            items(state.summaries, key = { it.subject }) { summary ->
                SubjectCard(
                    summary = summary,
                    onMark  = { status ->
                        viewModel.markAttendance(summary.subject, status, LocalDate.now())
                    }
                )
            }
        }
    }
}

// ── Overall banner ─────────────────────────────────────────────────────────────
@Composable
private fun OverallBanner(summaries: List<SubjectAttendanceSummary>) {
    val totalPresent = summaries.sumOf { it.present }
    val totalClasses = summaries.sumOf { it.total }
    val pct          = if (totalClasses == 0) 0f
    else (totalPresent.toFloat() / totalClasses) * 100f
    val safe         = pct >= 75f
    val bgColor      = if (safe) CardGreen else CardPeach
    val darkColor    = if (safe) CardGreenDark else CardPeachDark

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Overall Average",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkColor.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (totalClasses == 0) "--"
                    else "%.0f%%".format(pct),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = darkColor,
                    lineHeight = 52.sp
                )
                Spacer(Modifier.height(6.dp))
                // Mini stats row
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MiniStat(label = "Present", value = totalPresent.toString(), color = darkColor)
                    MiniStat(label = "Total",   value = totalClasses.toString(), color = darkColor)
                    MiniStat(
                        label = "Subjects",
                        value = summaries.size.toString(),
                        color = darkColor
                    )
                }
            }
            // Big status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(darkColor.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (safe) "Safe\nZone" else "At\nRisk",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = color.copy(alpha = 0.65f)
        )
    }
}

// ── Subject card ───────────────────────────────────────────────────────────────
@Composable
private fun SubjectCard(
    summary: SubjectAttendanceSummary,
    onMark: (AttendanceStatus) -> Unit
) {
    val standing = summary.standing()
    val needed   = summary.classesNeeded()

    val progress by animateFloatAsState(
        targetValue   = summary.percentage / 100f,
        animationSpec = tween(700),
        label         = "progress"
    )

    // Cycle cards through pastel colors based on standing
    val bgColor   = standing.cardBg
    val darkColor = standing.cardDark

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // ── Header row ────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = summary.subject,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 17.sp,
                        color      = darkColor,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text  = "${summary.present} present · ${summary.absent} absent",
                        fontSize = 12.sp,
                        color = darkColor.copy(alpha = 0.65f)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text       = if (summary.total == 0) "--"
                        else "%.0f%%".format(summary.percentage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 26.sp,
                        color      = darkColor
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(darkColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text      = standing.text,
                            color     = darkColor,
                            fontSize  = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Progress bar ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(darkColor.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(darkColor)
                )
            }

            // ── Classes needed hint ───────────────────────────────────
            if (needed > 0) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(darkColor.copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = darkColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Attend $needed more class${if (needed > 1) "es" else ""} to reach 75%",
                        color = darkColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Mark buttons ──────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Absent
                OutlinedButton(
                    onClick  = { onMark(AttendanceStatus.ABSENT) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, darkColor.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Absent", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }

                // Present
                Button(
                    onClick  = { onMark(AttendanceStatus.PRESENT) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = darkColor,
                        contentColor   = Color.White
                    )
                ) {
                    Text("Present", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            // Cancelled
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick  = { onMark(AttendanceStatus.CANCELLED) },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.textButtonColors(contentColor = darkColor.copy(alpha = 0.6f))
            ) {
                Text("Mark as Cancelled", fontSize = 13.sp)
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────
@Composable
private fun EmptyAttendanceState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CardPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📅", fontSize = 36.sp)
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text       = "No subjects yet",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A1A2E)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = "Add class slots in the Schedule tab first — subjects appear here automatically.",
                fontSize  = 14.sp,
                color     = Color(0xFF6B6B8A),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}