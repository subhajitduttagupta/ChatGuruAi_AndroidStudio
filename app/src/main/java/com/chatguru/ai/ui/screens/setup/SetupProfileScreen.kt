package com.chatguru.ai.ui.screens.setup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatguru.ai.model.Gender
import com.chatguru.ai.model.Language
import com.chatguru.ai.model.UserProfile
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.theme.*
import com.chatguru.ai.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
    onComplete: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    var selectedGender by remember { mutableStateOf(Gender.PREFER_NOT_TO_SAY) }
    var age by remember { mutableStateOf("25") }
    var selectedLanguage by remember { mutableStateOf(Language.ENGLISH) }

    // Animated entry
    val animationProgress by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Animated header
            AnimatedVisibility(
                visible = animationProgress.value > 0.3f,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column {
                    Text(
                        text = "Tell me about yourself",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Text(
                        text = "This helps us personalize your experience",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gender Selection
            AnimatedVisibility(
                visible = animationProgress.value > 0.4f,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { -100 })
            ) {
                GenderSelectionCard(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Age Input
            AnimatedVisibility(
                visible = animationProgress.value > 0.5f,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { 100 })
            ) {
                AgeInputCard(
                    age = age,
                    onAgeChange = { newAge ->
                        if (newAge.length <= 2) {
                            age = newAge.filter { it.isDigit() }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection
            AnimatedVisibility(
                visible = animationProgress.value > 0.6f,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { -100 })
            ) {
                LanguageSelectionCard(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Continue button
            AnimatedVisibility(
                visible = animationProgress.value > 0.7f,
                enter = fadeIn() + scaleIn()
            ) {
                GradientButton(
                    text = "Continue",
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 25
                        viewModel.saveUserProfile(
                            UserProfile(
                                gender = selectedGender,
                                age = ageInt,
                                preferredLanguage = selectedLanguage
                            )
                        )
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Gradients.pinkPurple,
                    icon = Icons.Default.ArrowForward
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GenderSelectionCard(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Gender.values().forEach { gender ->
                    GenderOptionCard(
                        gender = gender,
                        isSelected = selectedGender == gender,
                        onClick = { onGenderSelected(gender) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenderOptionCard(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardModifier = if (isSelected) {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(brush = Gradients.pinkPurple)
            .clickable(onClick = onClick)
    } else {
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = DarkCard)
            .clickable(onClick = onClick)
    }

    Box(modifier = cardModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = gender.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimaryDark
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun AgeInputCard(
    age: String,
    onAgeChange: (String) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Age",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = NeonPurple,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                ),
                singleLine = true,
                placeholder = {
                    Text("Enter your age", color = TextTertiaryDark)
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun LanguageSelectionCard(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Preferred Language",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Language.values().forEach { language ->
                    LanguageChipWithExample(
                        language = language,
                        isSelected = selectedLanguage == language,
                        onSelected = { onLanguageSelected(language) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageChipWithExample(
    language: Language,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Column {
        val chipModifier = if (isSelected) {
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(brush = Gradients.purpleBlue)
                .clickable(onClick = onSelected)
        } else {
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color = DarkCard)
                .clickable(onClick = onSelected)
        }

        Box(modifier = chipModifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = language.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextPrimaryDark
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Animate example text appearance
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = "Example: ${language.example}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )
        }
    }
}
