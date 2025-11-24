package com.chatguru.ai.ui.screens.conversation

import androidx.compose.ui.res.painterResource
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.chatguru.ai.model.CommentType
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.theme.*
import com.chatguru.ai.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chatId: String,
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showMoodDialog by remember { mutableStateOf(false) }

    val messages by viewModel.getCurrentMessages().collectAsState(initial = emptyList())
    val chat by viewModel.getCurrentChat().collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(chatId) {
        viewModel.setCurrentChat(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Chats",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        chat?.title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
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
                actions = {
                    // FIXED: Mood chip with cyan-blue gradient for visibility
                    val currentMoods by viewModel.getCurrentCommentTypes().collectAsState()
                    val selectedMood = currentMoods.firstOrNull() ?: CommentType.FRIEND

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(brush = Gradients.purpleBlue) // Changed from pinkPurple
                            .clickable { showMoodDialog = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = selectedMood.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = selectedMood.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Gradients.pinkPurple
                )
            )
        },
        bottomBar = {
            ConversationBottomBar(
                userInput = userInput,
                onInputChange = { userInput = it },
                selectedImageUri = selectedImageUri,
                onRemoveImage = { selectedImageUri = null },
                onPickImage = { imagePickerLauncher.launch("image/*") },
                onSend = {
                    if (userInput.isNotBlank() || selectedImageUri != null) {
                        viewModel.continueConversation(
                            chatId = chatId,
                            userReply = userInput,
                            imageUri = selectedImageUri,
                            language = userProfile.preferredLanguage,
                            context = context  // ✅ ADD THIS
                        )
                        userInput = ""
                        selectedImageUri = null
                    }
                },
                isLoading = uiState is MainViewModel.UiState.Loading
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { message ->
                    ChatBubbleWithActions(
                        message = message,
                        context = context,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AI Response", message.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onRetry = if (!message.isUser) {
                            {
                                viewModel.regenerateLastResponse(
                                    chatId = chatId,
                                    messageId = message.id,
                                    language = userProfile.preferredLanguage
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }

    // Change Mood Dialog
    if (showMoodDialog) {
        MoodSelectionDialog(
            currentMoods = viewModel.getCurrentCommentTypes().collectAsState().value,
            onDismiss = { showMoodDialog = false },
            onMoodSelected = { type ->
                viewModel.updateCommentMood(listOf(type))
                showMoodDialog = false
                Toast.makeText(
                    context,
                    "Mood changed to ${type.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
private fun ConversationBottomBar(
    userInput: String,
    onInputChange: (String) -> Unit,
    selectedImageUri: Uri?,
    onRemoveImage: () -> Unit,
    onPickImage: () -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        color = DarkCard,
        shadowElevation = 12.dp
    ) {
        Column {
            selectedImageUri?.let { uri ->
                GlassCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = onRemoveImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = NeonPink
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlowIconButton(
                    icon = Icons.Default.Image,
                    onClick = onPickImage,
                    tint = NeonPink
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = userInput,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Type your message...",
                            color = TextTertiaryDark
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = NeonPink,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(12.dp))

                // FIXED: Send button without white square
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(brush = Gradients.pinkPurple)
                        .clickable(onClick = onSend),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodSelectionDialog(
    currentMoods: List<CommentType>,
    onDismiss: () -> Unit,
    onMoodSelected: (CommentType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Change Mood",
                fontWeight = FontWeight.Bold,
                color = NeonPink
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Select comment type for next responses:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CommentType.values().forEach { type ->
                    val isSelected = currentMoods.contains(type)

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMoodSelected(type) },
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = if (isSelected) {
                            type.color.copy(alpha = 0.3f)
                        } else {
                            Color(0x20FFFFFF)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(type.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = null,
                                    tint = type.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) type.color else TextPrimaryDark
                                )
                                if (isSelected) {
                                    Text(
                                        text = "Currently active",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = type.color.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = type.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NeonPink)
            }
        },
        containerColor = DarkCard
    )
}

// FIXED: Image loading with persistableUri grant
@Composable
fun ChatBubbleWithActions(
    message: com.chatguru.ai.data.local.entity.MessageEntity,
    context: Context,
    onCopy: () -> Unit = {},
    onRetry: (() -> Unit)? = null
) {
    val isImage = message.text == "[Image]"
    val imageUri = if (isImage && message.imageUri != null) {
        Uri.parse(message.imageUri)
    } else null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        val bubbleModifier = if (message.isUser) {
            Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 4.dp
                    )
                )
                .background(brush = Gradients.pinkPurple)
        } else {
            Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(color = DarkCard)
        }

        Box(modifier = bubbleModifier) {
            Column {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!message.isUser) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // FIXED: Use ImageRequest with crossfade for persistent images
                    if (imageUri != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "User image",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit,
                                error = painterResource(android.R.drawable.ic_menu_report_image)
                            )
                        }
                    }

                    if (!isImage || message.text != "[Image]") {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isUser) Color.White else TextPrimaryDark
                        )
                    }
                }

                if (!message.isUser) {
                    HorizontalDivider(
                        color = GlassBorder,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = onCopy,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = NeonPink,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        onRetry?.let { retry ->
                            IconButton(
                                onClick = retry,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Try Again",
                                    tint = NeonOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
