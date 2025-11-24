package com.chatguru.ai.data.remote

import com.chatguru.ai.model.CommentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {

    @POST("generate-comment")
    suspend fun generateComment(
        @Body request: GenerateCommentRequest
    ): Response<CommentResponse>

    @POST("continue-conversation")
    suspend fun continueConversation(
        @Body request: ContinueConversationRequest
    ): Response<CommentResponse>
}
