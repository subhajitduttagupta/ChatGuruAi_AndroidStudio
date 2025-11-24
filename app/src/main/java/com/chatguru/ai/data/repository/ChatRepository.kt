package com.chatguru.ai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.chatguru.ai.data.local.dao.ChatDao
import com.chatguru.ai.data.local.dao.MessageDao
import com.chatguru.ai.data.local.entity.ChatEntity
import com.chatguru.ai.data.local.entity.MessageEntity
import com.chatguru.ai.data.remote.ChatApiService
import com.chatguru.ai.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
//import java.io.File
//import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import com.chatguru.ai.data.remote.GenerateCommentRequest
import com.chatguru.ai.data.remote.ContinueConversationRequest



@Singleton
class ChatRepository @Inject constructor(
    private val apiService: ChatApiService,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    @ApplicationContext private val context: Context
) {

    // Database operations
    fun getAllChats(): Flow<List<ChatEntity>> = chatDao.getAllChats()
    fun getChatById(chatId: String): Flow<ChatEntity?> = chatDao.getChatById(chatId)
    fun getFavoriteChats(): Flow<List<ChatEntity>> = chatDao.getFavoriteChats()
    fun getMessagesByChatId(chatId: String): Flow<List<MessageEntity>> =
        messageDao.getMessagesByChatId(chatId)

    suspend fun saveChat(chat: ChatEntity) = chatDao.insertChat(chat)
    suspend fun updateChat(chat: ChatEntity) = chatDao.updateChat(chat)
    suspend fun deleteChat(chat: ChatEntity) {
        chatDao.deleteChat(chat)
        messageDao.deleteMessagesByChatId(chat.id)
    }
    suspend fun saveMessage(message: MessageEntity) = messageDao.insertMessage(message)
    suspend fun deleteAllChats() {
        chatDao.deleteAllChats()
        messageDao.deleteAllMessages()
    }

    /**
     * Generate initial comment via backend API
     */
    suspend fun generateComment(
        imageUri: Uri?,
        postText: String?,
        commentTypes: List<CommentType>,
        language: Language,
        userProfile: UserProfile
    ): Result<String> {
        return try {
            // Convert image to base64 if exists
            val imageBase64 = imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return Result.failure(Exception("Cannot open image"))

                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                // Compress to reduce size
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                bitmap.recycle()

                // Convert to base64
                Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            }

            // Create request
            val request = GenerateCommentRequest(
                text = postText,
                tone = commentTypes.joinToString(", ") { it.name },
                language = language.displayName,
                gender = userProfile.gender.displayName,
                age = userProfile.age,
                imageBase64 = imageBase64
            )

            Log.d("ChatRepository", "Sending generate request: tone=${request.tone}, lang=${request.language}")

            // Call API
            val response = apiService.generateComment(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val comment = response.body()!!.comment
                Log.d("ChatRepository", "Generated successfully: $comment")
                Result.success(comment)
            } else {
                val errorMsg = response.body()?.error
                    ?: response.errorBody()?.string()
                    ?: "Failed to generate comment"
                Log.e("ChatRepository", "Generate failed: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Generate exception: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    suspend fun continueConversationWithMood(
        chatId: String,
        userReply: String,
        imageUri: Uri?,
        conversationHistory: List<Message>,
        language: Language,
        commentTypes: List<String>,
        userProfile: UserProfile
    ): Result<String> {
        return try {
            // If image is provided, use generateComment with conversation context
            if (imageUri != null) {
                // Convert image to base64
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: return Result.failure(Exception("Cannot open image"))

                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                bitmap.recycle()

                val imageBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                // Build context from conversation history
                val contextText = buildString {
                    append("Previous conversation:\n")
                    conversationHistory.takeLast(6).forEach {
                        append("${if (it.isUser) "User" else "AI"}: ${it.text}\n")
                    }
                    if (userReply.isNotBlank()) {
                        append("\nUser's new message: $userReply")
                    }
                }

                // Use generateComment with context
                val request = GenerateCommentRequest(
                    text = contextText,
                    tone = commentTypes.joinToString(", "),
                    language = language.displayName,
                    gender = userProfile.gender.displayName,
                    age = userProfile.age,
                    imageBase64 = imageBase64
                )

                val response = apiService.generateComment(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    Result.success(response.body()!!.comment)
                } else {
                    val errorMsg = response.body()?.error
                        ?: response.errorBody()?.string()
                        ?: "Failed to continue conversation"
                    Result.failure(Exception(errorMsg))
                }
            } else {
                // No image - text only conversation
                val historyJson = JSONArray().apply {
                    conversationHistory.takeLast(10).forEach {
                        put("${if (it.isUser) "User" else "AI"}: ${it.text}")
                    }
                }.toString()

                val request = ContinueConversationRequest(
                    chatId = chatId,
                    userReply = userReply,
                    history = historyJson,
                    language = language.displayName
                )

                Log.d("ChatRepository", "Continuing conversation: chatId=$chatId")

                val response = apiService.continueConversation(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val comment = response.body()!!.comment
                    Log.d("ChatRepository", "Continued successfully: $comment")
                    Result.success(comment)
                } else {
                    val errorMsg = response.body()?.error
                        ?: response.errorBody()?.string()
                        ?: "Failed to continue conversation"
                    Log.e("ChatRepository", "Continue failed: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Continue exception: ${e.message}", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

