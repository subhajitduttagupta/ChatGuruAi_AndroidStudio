package com.chatguru.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chatguru.ai.ui.theme.*

/**
 * Glass Card with glassmorphism effect
 * Features: Semi-transparent background, subtle border, blur effect
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0x30FFFFFF),
    borderColor: Color = GlassBorder,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Glass Card with gradient background
 */
@Composable
fun GradientGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    gradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0x40FFFFFF),
            Color(0x10FFFFFF)
        )
    ),
    borderColor: Color = GlassBorder,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = gradient)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Dark Glass Card for surfaces
 */
@Composable
fun DarkGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(DarkCard)
            .border(
                width = 1.dp,
                color = Color(0x20FFFFFF),
                shape = shape
            )
    ) {
        content()
    }
}
