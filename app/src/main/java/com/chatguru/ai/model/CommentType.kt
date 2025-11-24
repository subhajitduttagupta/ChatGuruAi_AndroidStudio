package com.chatguru.ai.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.chatguru.ai.ui.theme.*

enum class CommentType(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    FRIEND("Friend", Icons.Default.Groups, FriendColor),
    FLIRTY("Flirty", Icons.Default.Favorite, FlirtyColor),
    FUNNY("Funny", Icons.Default.SentimentVerySatisfied, FunnyColor),
    INTELLIGENT("Intelligent", Icons.Default.Lightbulb, IntelligentColor),
    POETIC("Poetic", Icons.Default.Edit, PoeticColor),
    COMPLIMENT("Compliment", Icons.Default.AutoAwesome, ComplimentColor),
    ROMANTIC("Romantic", Icons.Default.VolunteerActivism, RomanticColor)
}
