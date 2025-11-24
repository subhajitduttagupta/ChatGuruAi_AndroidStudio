package com.chatguru.ai.data.remote

import com.google.gson.annotations.SerializedName

data class GenerateCommentRequest(
    @SerializedName("text")
    val text: String?,

    @SerializedName("tone")
    val tone: String,

    @SerializedName("language")
    val language: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("age")
    val age: Int,

    @SerializedName("imageBase64")
    val imageBase64: String? = null
)

data class ContinueConversationRequest(
    @SerializedName("chatId")
    val chatId: String?,

    @SerializedName("userReply")
    val userReply: String,

    @SerializedName("history")
    val history: String,

    @SerializedName("language")
    val language: String
)
