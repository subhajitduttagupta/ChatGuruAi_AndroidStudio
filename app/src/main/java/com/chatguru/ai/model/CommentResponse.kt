package com.chatguru.ai.model

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("comment")
    val comment: String,

    @SerializedName("chatId")
    val chatId: String? = null,

    @SerializedName("error")
    val error: String? = null
)
