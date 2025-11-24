package com.chatguru.ai.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chatguru.ai.ui.theme.*
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LightPink, LightLavender)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(
                    page = when (page) {
                        0 -> OnboardingData(
                            icon = Icons.Default.Chat,
                            title = "Smart AI Comments",
                            description = "Generate intelligent, witty, and personalized comments for social media posts"
                        )
                        1 -> OnboardingData(
                            icon = Icons.Default.AutoAwesome,
                            title = "Multiple Styles",
                            description = "Choose from Friend, Flirty, Funny, Intelligent, Poetic, Compliment, and Romantic tones"
                        )
                        else -> OnboardingData(
                            icon = Icons.Default.Language,
                            title = "Multi-Language Support",
                            description = "Express yourself in English, Hindi, Hinglish, Bengali, and more"
                        )
                    }
                )
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(16.dp),
                activeColor = PrimaryPink,
                inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )

            Button(
                onClick = {
                    if (pagerState.currentPage == 2) {
                        onComplete()
                    } else {
                        scope.launch {  // ADD 'scope' here
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPink
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "Get Started" else "Next",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(page: OnboardingData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = PrimaryPink
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
    }
}

data class OnboardingData(
    val icon: ImageVector,
    val title: String,
    val description: String
)
