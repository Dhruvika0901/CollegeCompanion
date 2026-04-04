//package com.example.collegecompanion.ui.theme
//
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//
//// ── Brand Palette ──────────────────────────────────────────────
//val Purple100   = Color(0xFFC4B5FD)
//val Purple200   = Color(0xFFA78BFA)
//val Purple700   = Color(0xFF7C3AED)
//
//val Blue400     = Color(0xFF60A5FA)
//val Blue500     = Color(0xFF3B7EFF)
//
//val Red400      = Color(0xFFFF4D6D)
//val Amber400    = Color(0xFFFFD160)
//val Green400    = Color(0xFF4ADE80)
//val Orange400   = Color(0xFFF97316)
//
//val BgPrimary   = Color(0xFF0F0F14)
//val BgCard      = Color(0xFF1A1928)
//val BgSurface   = Color(0xFF1E1C32)
//val BgNavBar    = Color(0xFF13121F)
//
//val TextPrimary   = Color(0xFFF0EEFF)
//val TextSecondary = Color(0xFFCCC8F0)
//val TextMuted     = Color(0xFF7B7A9E)
//val TextDim       = Color(0xFF5A5878)
//
//val Border        = Color(0xFF2E2C48)
//
//// ── Priority Colors ────────────────────────────────────────────
//val PriorityHigh   = Red400
//val PriorityMedium = Amber400
//val PriorityLow    = Green400
//
//// ── Dark Color Scheme ──────────────────────────────────────────
//private val DarkColorScheme = darkColorScheme(
//    primary          = Purple200,
//    onPrimary        = Color(0xFF1A0050),
//    primaryContainer = Color(0xFF251F40),
//    onPrimaryContainer = Purple100,
//    secondary        = Blue400,
//    onSecondary      = Color(0xFF00214A),
//    secondaryContainer = Color(0xFF1A2A40),
//    onSecondaryContainer = Color(0xFFBDE0FF),
//    background       = BgPrimary,
//    onBackground     = TextPrimary,
//    surface          = BgCard,
//    onSurface        = TextPrimary,
//    surfaceVariant   = BgSurface,
//    onSurfaceVariant = TextSecondary,
//    outline          = Border,
//    error            = Red400,
//)
//
//@Composable
//fun CollegeCompanionTheme(content: @Composable () -> Unit) {
//    MaterialTheme(
//        colorScheme = DarkColorScheme,
//        typography  = AppTypography,
//        content     = content
//    )
//}