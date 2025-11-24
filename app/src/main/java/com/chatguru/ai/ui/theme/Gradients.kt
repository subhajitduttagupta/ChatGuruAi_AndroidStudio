package com.chatguru.ai.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {

    // ADD THIS ONE 👇
    val orangePurple = Brush.horizontalGradient(
        colors = listOf(NeonOrange, NeonPurple)
    )
    // === PRIMARY GRADIENTS ===
    val pinkPurple = Brush.linearGradient(
        colors = listOf(NeonPink, NeonPurple),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    val purpleBlue = Brush.linearGradient(
        colors = listOf(NeonPurple, NeonBlue),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    val pinkOrange = Brush.linearGradient(
        colors = listOf(NeonPink, NeonOrange),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    val blueGreen = Brush.linearGradient(
        colors = listOf(NeonBlue, NeonCyan),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    val orangeYellow = Brush.linearGradient(
        colors = listOf(NeonOrange, GradientYellow),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    // === BACKGROUND GRADIENTS ===
    val darkBackground = Brush.verticalGradient(
        colors = listOf(
            DarkBackground,
            Color(0xFF0F0F16)
        )
    )

    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x40FFFFFF),
            Color(0x10FFFFFF)
        )
    )

    // === GLASSMORPHISM ===
    val glassLight = Brush.verticalGradient(
        colors = listOf(
            GlassWhite,
            GlassHighlight
        )
    )

    val glassDark = Brush.verticalGradient(
        colors = listOf(
            Color(0x40000000),
            Color(0x10000000)
        )
    )

    // === BUTTON GRADIENTS ===
    val primaryButton = Brush.horizontalGradient(
        colors = listOf(NeonPink, NeonPurple)
    )

    val secondaryButton = Brush.horizontalGradient(
        colors = listOf(NeonBlue, NeonCyan)
    )

    val accentButton = Brush.horizontalGradient(
        colors = listOf(NeonOrange, GradientYellow)
    )
}
