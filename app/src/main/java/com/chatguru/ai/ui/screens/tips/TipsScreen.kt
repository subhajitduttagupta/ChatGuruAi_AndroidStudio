package com.chatguru.ai.ui.screens.tips

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tips & Tricks",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    GlowIconButton(
                        icon = Icons.Default.ArrowBack,
                        onClick = { navController.navigateUp() },
                        tint = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Gradients.orangePurple
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    backgroundColor = Color(0x30FF6B35)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    brush = Gradients.orangePurple
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pro Tips",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Master Chat Guru AI",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tips with staggered animation
                val tips = remember {
                    listOf(
                        TipData(
                            Icons.Default.AutoAwesome,
                            "Choose Multiple Tones",
                            "Select multiple comment types (e.g., Funny + Friendly) to get a unique blend of styles in your AI-generated comment.",
                            NeonPink
                        ),
                        TipData(
                            Icons.Default.Image,
                            "Upload Clear Screenshots",
                            "For best results, upload high-quality screenshots where text and images are clearly visible. This helps the AI understand the context better.",
                            NeonPurple
                        ),
                        TipData(
                            Icons.Default.Chat,
                            "Continue Conversations",
                            "Use the 'Continue Chat' feature to have natural back-and-forth conversations. The AI remembers previous messages for contextual replies.",
                            NeonBlue
                        ),
                        TipData(
                            Icons.Default.Language,
                            "Mix Languages",
                            "Try Hinglish or Binglish for a perfect blend of English and regional languages. Great for casual, relatable comments!",
                            NeonCyan
                        ),
                        TipData(
                            Icons.Default.Lightbulb,
                            "Be Specific",
                            "If you paste post text, include emojis and hashtags. The more context you provide, the more relevant the AI's response will be.",
                            NeonOrange
                        ),
                        TipData(
                            Icons.Default.Favorite,
                            "Save Favorites",
                            "Mark your best conversations as favorites for quick access later. Perfect for reusing great comment templates!",
                            NeonPink
                        ),
                        TipData(
                            Icons.Default.Edit,
                            "Customize Before Posting",
                            "Always review and personalize AI-generated comments before posting. Add your own touch to make them authentic!",
                            NeonPurple
                        ),
                        TipData(
                            Icons.Default.Security,
                            "Privacy First",
                            "All your chats are stored locally on your device. We don't upload or share your personal conversations.",
                            NeonCyan
                        )
                    )
                }

                tips.forEachIndexed { index, tip ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 80
                            )
                        ) + slideInHorizontally(
                            initialOffsetX = { if (index % 2 == 0) -100 else 100 },
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 80
                            )
                        )
                    ) {
                        TipCard(
                            icon = tip.icon,
                            title = tip.title,
                            description = tip.description,
                            color = tip.color
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

private data class TipData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun TipCard(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color = NeonPink
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon box with glow
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}
