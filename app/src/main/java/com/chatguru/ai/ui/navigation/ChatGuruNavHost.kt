package com.chatguru.ai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chatguru.ai.ui.screens.about.AboutScreen
import com.chatguru.ai.ui.screens.conversation.ConversationScreen
import com.chatguru.ai.ui.screens.favorites.FavoritesScreen
import com.chatguru.ai.ui.screens.home.HomeScreen
import com.chatguru.ai.ui.screens.onboarding.OnboardingScreen
import com.chatguru.ai.ui.screens.previouschats.PreviousChatsScreen
import com.chatguru.ai.ui.screens.settings.SettingsScreen
import com.chatguru.ai.ui.screens.setup.SetupProfileScreen
import com.chatguru.ai.ui.screens.startchat.StartChatScreen
import com.chatguru.ai.ui.screens.tips.TipsScreen
import com.chatguru.ai.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatGuruNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    // Use remember with a suspended computation to get initial values
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val isOnboardingCompleted = mainViewModel.isOnboardingCompleted.first()
        val isProfileSetupCompleted = mainViewModel.isProfileSetupCompleted.first()

        startDestination = when {
            !isOnboardingCompleted -> Screen.Onboarding.route
            !isProfileSetupCompleted -> Screen.SetupProfile.route
            else -> Screen.Home.route
        }
    }

    // Don't render NavHost until startDestination is determined
    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            initialOffsetX = { 300 },
                            animationSpec = tween(300)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(
                            targetOffsetX = { -300 },
                            animationSpec = tween(300)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            initialOffsetX = { -300 },
                            animationSpec = tween(300)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(
                            targetOffsetX = { 300 },
                            animationSpec = tween(300)
                        )
            }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        mainViewModel.completeOnboarding()
                        navController.navigate(Screen.SetupProfile.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.SetupProfile.route) {
                SetupProfileScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SetupProfile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }

            composable(Screen.StartChat.route) {
                StartChatScreen(navController = navController)
            }

            composable(
                route = Screen.Conversation.route,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
                ConversationScreen(
                    chatId = chatId,
                    navController = navController
                )
            }

            composable(Screen.PreviousChats.route) {
                PreviousChatsScreen(navController = navController)
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(navController = navController)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            composable(Screen.Tips.route) {
                TipsScreen(navController = navController)
            }

            composable(Screen.About.route) {
                AboutScreen(navController = navController)
            }
        }
    }
}
