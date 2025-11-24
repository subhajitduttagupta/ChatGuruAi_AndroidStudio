package com.chatguru.ai.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object SetupProfile : Screen("setup_profile")
    object Home : Screen("home")
    object StartChat : Screen("start_chat")
//    object GeneratedComment : Screen("generated_comment/{chatId}") {
//        fun createRoute(chatId: String) = "generated_comment/$chatId"
//    }
    object Conversation : Screen("conversation/{chatId}") {
        fun createRoute(chatId: String) = "conversation/$chatId"
    }
    object PreviousChats : Screen("previous_chats")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Tips : Screen("tips")
    object About : Screen("about")
}
