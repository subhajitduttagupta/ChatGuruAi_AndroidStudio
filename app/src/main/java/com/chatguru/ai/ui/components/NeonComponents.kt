package com.chatguru.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chatguru.ai.ui.theme.*

/**
 * Neon icon button with glow (renamed to avoid conflict)
 */
@Composable
fun GlowIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = NeonBlue,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x30FFFFFF),
                        Color(0x10FFFFFF)
                    )
                )
            )
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Neon mood chip for comment types
 */
@Composable
fun NeonMoodChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    gradient: Brush = Gradients.pinkPurple,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = if (isSelected) {
        Modifier
            .fillMaxSize()
            .background(brush = gradient)
    } else {
        Modifier
            .fillMaxSize()
            .background(color = Color(0x20FFFFFF))
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(44.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = backgroundModifier,
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondaryDark
                )
            }
        }
    }
}

/**
 * Neon badge for notifications/counters
 */
@Composable
fun NeonBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeonPink
) {
    if (count > 0) {
        Surface(
            modifier = modifier
                .size(20.dp),
            shape = RoundedCornerShape(10.dp),
            color = backgroundColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = if (count > 9) "9+" else count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Neon toggle switch
 */
@Composable
fun NeonSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = NeonPink,
            uncheckedThumbColor = Color.Gray,
            uncheckedTrackColor = DarkCard
        )
    )
}
