package com.chatguru.ai.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chatguru.ai.model.Gender
import com.chatguru.ai.model.Language
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.theme.*
import com.chatguru.ai.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showGenderDialog by remember { mutableStateOf(false) }
    var showAgeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Account Section
                SettingsSection(title = "Account") {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            SettingsItem(
                                icon = Icons.Default.Person,
                                title = "Gender",
                                subtitle = userProfile.gender.displayName,
                                iconColor = NeonPink,
                                onClick = { showGenderDialog = true }
                            )
                            HorizontalDivider(color = GlassBorder)
                            SettingsItem(
                                icon = Icons.Default.Cake,
                                title = "Age",
                                subtitle = "${userProfile.age} years",
                                iconColor = NeonPurple,
                                onClick = { showAgeDialog = true }
                            )
                        }
                    }
                }

                // Preferences Section
                SettingsSection(title = "Preferences") {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        SettingsItem(
                            icon = Icons.Default.Language,
                            title = "Language",
                            subtitle = userProfile.preferredLanguage.displayName,
                            iconColor = NeonBlue,
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }

                // Data Section
                SettingsSection(title = "Data Management") {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            SettingsItem(
                                icon = Icons.Default.DeleteSweep,
                                title = "Clear Chat History",
                                subtitle = "Delete all conversations",
                                iconColor = NeonOrange,
                                onClick = { showDeleteDialog = true }
                            )
                            HorizontalDivider(color = GlassBorder)
                            SettingsItem(
                                icon = Icons.Default.RestartAlt,
                                title = "Reset App",
                                subtitle = "Clear all data and start fresh",
                                iconColor = NeonRed,
                                onClick = { showResetDialog = true }
                            )
                        }
                    }
                }

                // App Info Card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0x30FFFFFF)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chat Guru AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showGenderDialog) {
        GenderSelectionDialog(
            currentGender = userProfile.gender,
            onDismiss = { showGenderDialog = false },
            onConfirm = { gender ->
                viewModel.saveUserProfile(userProfile.copy(gender = gender))
                showGenderDialog = false
            }
        )
    }

    if (showAgeDialog) {
        AgeInputDialog(
            currentAge = userProfile.age,
            onDismiss = { showAgeDialog = false },
            onConfirm = { age ->
                viewModel.saveUserProfile(userProfile.copy(age = age))
                showAgeDialog = false
            }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = userProfile.preferredLanguage,
            onDismiss = { showLanguageDialog = false },
            onSelect = { language ->
                viewModel.updateLanguage(language)
                showLanguageDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Clear Chat History",
            message = "Are you sure you want to delete all chat history? This action cannot be undone.",
            confirmText = "Delete",
            confirmColor = NeonRed,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteAllChats()
                showDeleteDialog = false
            }
        )
    }

    if (showResetDialog) {
        ConfirmationDialog(
            title = "Reset App",
            message = "This will clear all data including chat history and settings. The app will restart. Continue?",
            confirmText = "Reset",
            confirmColor = NeonRed,
            onDismiss = { showResetDialog = false },
            onConfirm = {
                viewModel.resetApp()
                showResetDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = NeonPink,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = TextPrimaryDark
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextTertiaryDark,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Dialog Components
@Composable
private fun GenderSelectionDialog(
    currentGender: Gender,
    onDismiss: () -> Unit,
    onConfirm: (Gender) -> Unit
) {
    var selectedGender by remember { mutableStateOf(currentGender) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Gender",
                fontWeight = FontWeight.Bold,
                color = NeonPink
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.values().forEach { gender ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGender = gender },
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = if (selectedGender == gender) {
                            NeonPink.copy(alpha = 0.2f)
                        } else {
                            Color(0x20FFFFFF)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = gender.displayName,
                                color = TextPrimaryDark
                            )
                            if (selectedGender == gender) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedGender) }) {
                Text("Save", color = NeonPink, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun AgeInputDialog(
    currentAge: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var ageText by remember { mutableStateOf(currentAge.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Set Age",
                fontWeight = FontWeight.Bold,
                color = NeonPurple
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter your age (12-99)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        if (it.length <= 2) {
                            ageText = it.filter { char -> char.isDigit() }
                        }
                    },
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
                    placeholder = { Text("e.g., 25", color = TextTertiaryDark) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val age = ageText.toIntOrNull()
                    if (age != null && age in 12..99) {
                        onConfirm(age)
                    }
                }
            ) {
                Text("Save", color = NeonPurple, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onSelect: (Language) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Language",
                fontWeight = FontWeight.Bold,
                color = NeonBlue
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Language.values().forEach { language ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(language) },
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = if (currentLanguage == language) {
                            NeonBlue.copy(alpha = 0.2f)
                        } else {
                            Color(0x20FFFFFF)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = language.displayName,
                                    fontWeight = if (currentLanguage == language) FontWeight.Bold else FontWeight.Normal,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = language.example,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                                )
                            }
                            if (currentLanguage == language) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NeonBlue, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = confirmColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = confirmColor
                )
            }
        },
        text = {
            Text(message, color = TextPrimaryDark)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = confirmColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(20.dp)
    )
}
