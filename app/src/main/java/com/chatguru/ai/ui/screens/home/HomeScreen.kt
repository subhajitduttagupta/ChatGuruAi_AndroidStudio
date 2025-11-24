package com.chatguru.ai.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.chatguru.ai.R
import com.chatguru.ai.ui.components.*
import com.chatguru.ai.ui.navigation.Screen
import com.chatguru.ai.ui.theme.*
import com.chatguru.ai.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val recentChats by viewModel.allChats.collectAsState(initial = emptyList())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onNavigate = { route ->
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Chat Guru AI",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    },
                    navigationIcon = {
                        // FIXED: Blue gradient rectangle hamburger button
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush = Gradients.purpleBlue)
                                .clickable { scope.launch { drawerState.open() } },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TOP SECTION: Welcome + Logo + Buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)   // <-- Left + Right padding
                    ) {
                        // FIXED: Static logo without animation and border
                        StaticLogo()

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Welcome to Chat Guru AI",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Generate smart, witty comments for any post",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondaryDark
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Main action button
                        GradientButton(
                            text = "Start New Chat",
                            onClick = { navController.navigate(Screen.StartChat.route) },
                            modifier = Modifier.fillMaxWidth(),
                            gradient = Gradients.pinkPurple,
                            icon = Icons.Default.Add
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Secondary button
                        OutlinedGradientButton(
                            text = "Previous Chats",
                            onClick = { navController.navigate(Screen.PreviousChats.route) },
                            modifier = Modifier.fillMaxWidth(),
                            gradient = Gradients.purpleBlue,
                            icon = Icons.Default.History
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // RECENT CHATS SECTION
                    if (recentChats.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Chats",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                TextButton(
                                    onClick = { navController.navigate(Screen.PreviousChats.route) }
                                ) {
                                    Text("View All", color = NeonPink)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = NeonPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            recentChats.take(3).forEach { chat ->
                                RecentChatCard(
                                    chat = chat,
                                    onClick = {
                                        navController.navigate(Screen.Conversation.createRoute(chat.id))
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// FIXED: Static logo without animation or thick border
@Composable
private fun StaticLogo() {
//    Box(
//        modifier = Modifier
//            .size(120.dp)
//            .clip(RoundedCornerShape(30.dp))
//            .background(brush = Gradients.pinkPurple),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.chatguru),
//            contentDescription = "Chat Guru Logo",
//            modifier = Modifier
//                .size(80.dp)
//                .padding(8.dp),
//            contentScale = ContentScale.Fit
//        )
//    }
    Image(
        painter = painterResource(id = R.drawable.chatguru),
        contentDescription = "Logo",
        modifier = Modifier.size(160.dp), // or any size you want
        contentScale = ContentScale.Fit
    )
}

// FIXED: Removed shadow from chat cards
@Composable
fun RecentChatCard(
    chat: com.chatguru.ai.data.local.entity.ChatEntity,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = chat.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (chat.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatRecentTime(chat.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiaryDark
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open chat",
                tint = NeonPink,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

fun formatRecentTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}

@Composable
fun AppDrawer(onNavigate: (String) -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Drawer header with logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(brush = Gradients.pinkPurple),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Image(
                        painter = painterResource(id = R.drawable.chatguru),
                        contentDescription = "Logo",
                        modifier = Modifier.size(140.dp), // or any size you want
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chat Guru AI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            DrawerItem(
                icon = Icons.Default.Add,
                title = "Start New Chat",
                onClick = { onNavigate(Screen.StartChat.route) }
            )

            DrawerItem(
                icon = Icons.Default.History,
                title = "Previous Chats",
                onClick = { onNavigate(Screen.PreviousChats.route) }
            )

            DrawerItem(
                icon = Icons.Default.Favorite,
                title = "Favorites",
                onClick = { onNavigate(Screen.Favorites.route) }
            )

            GlowingDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = NeonPurple
            )

            DrawerItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = { onNavigate(Screen.Settings.route) }
            )

            DrawerItem(
                icon = Icons.Default.Lightbulb,
                title = "Tips & Tricks",
                onClick = { onNavigate(Screen.Tips.route) }
            )

            DrawerItem(
                icon = Icons.Default.Info,
                title = "About",
                onClick = { onNavigate(Screen.About.route) }
            )
        }
    }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimaryDark
            )
        }
    }
}
