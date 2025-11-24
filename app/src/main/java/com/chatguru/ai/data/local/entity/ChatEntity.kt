package com.chatguru.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val screenshotUri: String?,
    val postText: String?,
    val lastMessage: String,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val initialImageUri: String? = null,  // ADD THIS: Store the first image
    val selectedLanguage: String = "ENGLISH",  // ADD THIS: Store selected language
    val selectedCommentTypes: String = "FRIEND"  // ADD THIS: Store selected moods
)
