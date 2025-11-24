package com.chatguru.ai.ui.screens.startchat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chatguru.ai.model.CommentType
import com.chatguru.ai.model.Language
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.navigation.Screen
import com.chatguru.ai.ui.theme.*
import com.chatguru.ai.ui.viewmodel.MainViewModel
import com.chatguru.ai.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var postText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<CommentType?>(null) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var selectedLanguage by remember { mutableStateOf(userProfile.preferredLanguage) }

    LaunchedEffect(userProfile.preferredLanguage) {
        selectedLanguage = userProfile.preferredLanguage
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        if (!PermissionUtils.hasStoragePermission(context)) {
            permissionLauncher.launch(PermissionUtils.getStoragePermissions())
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(uiState) {
        if (uiState is MainViewModel.UiState.Success) {
            val chatId = (uiState as MainViewModel.UiState.Success).chatId
            navController.navigate(Screen.Conversation.createRoute(chatId)) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
            viewModel.resetUiState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Start New Chat",
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Image upload card
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clickable {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            brush = Gradients.pinkPurple
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Upload a screenshot",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap to select image",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }

                // Post caption section
                Column {
                    Text(
                        text = "Or paste the caption here",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            placeholder = {
                                Text(
                                    "Paste post caption...",
                                    color = TextTertiaryDark
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = NeonPink,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            ),
                            maxLines = 6
                        )
                    }
                }

                // Comment type section
                Column {
                    Text(
                        text = "Comment Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val commentTypes = CommentType.values().toList()
                    commentTypes.chunked(2).forEach { rowTypes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowTypes.forEach { type ->
                                CommentTypeChip(
                                    type = type,
                                    isSelected = selectedType == type,
                                    onToggle = { selectedType = type },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowTypes.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Language selector
                Column {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = null,
                                    tint = NeonPurple,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = selectedLanguage.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimaryDark
                                )
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = TextSecondaryDark
                            )
                        }
                    }
                }

                // Generate button
                GradientButton(
                    text = if (uiState is MainViewModel.UiState.Loading) "Generating..." else "Generate Comment",
                    onClick = {
                        if ((imageUri != null || postText.isNotBlank()) && selectedType != null) {
                            viewModel.generateComment(
                                imageUri = imageUri,
                                postText = postText.ifBlank { null },
                                commentTypes = listOf(selectedType!!),
                                language = selectedLanguage,
                                context = context  // ✅ ADD THIS
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Gradients.pinkPurple,
                    icon = if (uiState !is MainViewModel.UiState.Loading) Icons.Default.AutoAwesome else null,
                    enabled = (imageUri != null || postText.isNotBlank()) && selectedType != null && uiState !is MainViewModel.UiState.Loading
                )

                if (uiState is MainViewModel.UiState.Error) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = NeonRed.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = NeonRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = (uiState as MainViewModel.UiState.Error).message,
                                color = NeonRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Language dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    "Select Language",
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Language.values().forEach { language ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = language
                                    showLanguageDialog = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = if (language == selectedLanguage) {
                                NeonPurple.copy(alpha = 0.2f)
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
                                Column {
                                    Text(
                                        text = language.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (language == selectedLanguage) FontWeight.Bold else FontWeight.Normal,
                                        color = TextPrimaryDark
                                    )
                                    Text(
                                        text = language.example,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryDark
                                    )
                                }
                                if (language == selectedLanguage) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = NeonPurple,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = NeonPurple)
                }
            },
            containerColor = DarkCard
        )
    }
}

@Composable
fun CommentTypeChip(
    type: CommentType,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipModifier = if (isSelected) {
        modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Gradients.pinkPurple
            )
            .clickable(onClick = onToggle)
    } else {
        modifier
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = DarkCard)
            .clickable(onClick = onToggle)
    }

    Box(modifier = chipModifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else type.color.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = type.displayName,
                    tint = if (isSelected) Color.White else type.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextPrimaryDark
            )
        }
    }
}
