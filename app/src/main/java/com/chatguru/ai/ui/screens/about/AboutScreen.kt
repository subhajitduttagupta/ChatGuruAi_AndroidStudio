package com.chatguru.ai.ui.screens.about

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    // Animation progress
    val animationProgress by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                    brush = Gradients.pinkPurple
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Animated logo
                AnimatedVisibility(
                    visible = animationProgress.value > 0.2f,
                    enter = fadeIn() + scaleIn()
                ) {
                    AnimatedGlowingLogo()
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App name and version
                AnimatedVisibility(
                    visible = animationProgress.value > 0.3f,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Chat Guru AI",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Description card
                AnimatedVisibility(
                    visible = animationProgress.value > 0.4f,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { -100 })
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your AI-Powered Comment Assistant",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chat Guru AI helps you create engaging, witty, and personalized comments for social media posts. Powered by Google's Gemini AI, we bring you intelligent comment generation in multiple languages and tones.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark,
                                textAlign = TextAlign.Center,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Features card
                AnimatedVisibility(
                    visible = animationProgress.value > 0.5f,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { 100 })
                ) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color(0x309D4EDD)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            FeatureItem(
                                icon = Icons.Default.AutoAwesome,
                                title = "AI-Powered",
                                description = "Gemini AI technology",
                                color = NeonPink
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FeatureItem(
                                icon = Icons.Default.Language,
                                title = "Multi-Language",
                                description = "5+ language options",
                                color = NeonBlue
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FeatureItem(
                                icon = Icons.Default.Security,
                                title = "Private & Secure",
                                description = "Local data storage",
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FeatureItem(
                                icon = Icons.Default.Speed,
                                title = "Fast & Efficient",
                                description = "Instant comment generation",
                                color = NeonOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Creator info
                AnimatedVisibility(
                    visible = animationProgress.value > 0.6f,
                    enter = fadeIn()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Made with ❤️ by",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subhajit D.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "© 2025 Chat Guru AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiaryDark,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "All rights reserved",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiaryDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AnimatedGlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size((130 * scale).dp)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink, NeonPurple, NeonBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.White
        )
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color = NeonPink
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
            )
        }
    }
}
