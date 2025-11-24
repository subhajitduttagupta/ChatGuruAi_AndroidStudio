package com.chatguru.ai.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatguru.ai.data.local.UserPreferencesManager
import com.chatguru.ai.data.local.entity.ChatEntity
import com.chatguru.ai.data.local.entity.MessageEntity
import com.chatguru.ai.data.repository.ChatRepository
import com.chatguru.ai.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    val isOnboardingCompleted: Flow<Boolean> = preferencesManager.isOnboardingCompleted
    val isProfileSetupCompleted: Flow<Boolean> = preferencesManager.isProfileSetupCompleted
    val userProfile: StateFlow<UserProfile> = preferencesManager.userProfile
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserProfile())

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId.asStateFlow()

    private val _lastGenerationParams = MutableStateFlow<GenerationParams?>(null)

    data class GenerationParams(
        val imageUri: Uri?,
        val postText: String?,
        val commentTypes: List<CommentType>
    )

    // ✅ Added language and mood tracking
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    private val _currentCommentTypes = MutableStateFlow<List<CommentType>>(listOf(CommentType.FRIEND))

    val allChats: Flow<List<ChatEntity>> = repository.getAllChats()
    val favoriteChats: Flow<List<ChatEntity>> = repository.getFavoriteChats()

    fun getCurrentChat(): Flow<ChatEntity?> {
        return _currentChatId.value?.let { repository.getChatById(it) } ?: flowOf(null)
    }

    fun getCurrentMessages(): Flow<List<MessageEntity>> {
        return _currentChatId.value?.let { repository.getMessagesByChatId(it) } ?: flowOf(emptyList())
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted()
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            preferencesManager.saveUserProfile(profile)
        }
    }

    // ✅ Updated generateComment with stored language & mood support


    fun generateComment(
        imageUri: Uri?,
        postText: String?,
        commentTypes: List<CommentType>,
        language: Language,
        context: android.content.Context  // ✅ ADD CONTEXT PARAMETER
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            _currentLanguage.value = language
            _currentCommentTypes.value = commentTypes
            _lastGenerationParams.value = GenerationParams(imageUri, postText, commentTypes)

            // ✅ GRANT PERSISTENT URI PERMISSION
            imageUri?.let { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    // Permission already granted or not available
                }
            }

            val result = repository.generateComment(
                imageUri = imageUri,
                postText = postText,
                commentTypes = commentTypes,
                language = language,
                userProfile = userProfile.value
            )

            result.fold(
                onSuccess = { comment ->
                    val chatId = UUID.randomUUID().toString()
                    _currentChatId.value = chatId

                    val chatTitle = generateChatTitle(postText, comment, imageUri != null)

                    val chat = ChatEntity(
                        id = chatId,
                        title = chatTitle,
                        screenshotUri = imageUri?.toString(),
                        postText = postText,
                        lastMessage = comment,
                        timestamp = System.currentTimeMillis(),
                        initialImageUri = imageUri?.toString(),
                        selectedLanguage = language.name,
                        selectedCommentTypes = commentTypes.joinToString(",") { it.name }
                    )
                    repository.saveChat(chat)

                    if (imageUri != null || postText?.isNotBlank() == true) {
                        val userMessage = MessageEntity(
                            id = UUID.randomUUID().toString(),
                            chatId = chatId,
                            text = postText ?: "[Image]",
                            isUser = true,
                            timestamp = System.currentTimeMillis(),
                            imageUri = imageUri?.toString()
                        )
                        repository.saveMessage(userMessage)
                    }

                    val aiMessage = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = comment,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveMessage(aiMessage)

                    _uiState.value = UiState.Success(comment, chatId)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }



    // ADD THIS NEW HELPER FUNCTION
    private fun generateChatTitle(postText: String?, aiComment: String, hasImage: Boolean): String {
        // Priority 1: Use post text if available (first 40 chars)
        if (!postText.isNullOrBlank()) {
            val cleanText = postText.trim()
            return if (cleanText.length > 40) {
                cleanText.take(40).trim() + "..."
            } else {
                cleanText
            }
        }

        // Priority 2: Use AI comment (first 40 chars)
        if (aiComment.isNotBlank()) {
            val cleanComment = aiComment.trim()
            return if (cleanComment.length > 40) {
                cleanComment.take(40).trim() + "..."
            } else {
                cleanComment
            }
        }

        // Priority 3: If only image, use descriptive title
        return if (hasImage) {
            "Image Chat"
        } else {
            "New Chat"
        }
    }

    // ✅ Load language and mood when switching chats
    fun setCurrentChat(chatId: String) {
        viewModelScope.launch {
            _currentChatId.value = chatId

            repository.getChatById(chatId).first()?.let { chat ->
                _currentLanguage.value = try {
                    Language.valueOf(chat.selectedLanguage)
                } catch (e: Exception) {
                    Language.ENGLISH
                }

                _currentCommentTypes.value = try {
                    chat.selectedCommentTypes.split(",").mapNotNull { typeName ->
                        CommentType.values().find { it.name == typeName }
                    }
                } catch (e: Exception) {
                    listOf(CommentType.FRIEND)
                }
            }
        }
    }

    // ✅ Persist mood updates
    fun updateCommentMood(commentTypes: List<CommentType>) {
        _currentCommentTypes.value = commentTypes

        viewModelScope.launch {
            _currentChatId.value?.let { chatId ->
                repository.getChatById(chatId).first()?.let { chat ->
                    repository.updateChat(
                        chat.copy(
                            selectedCommentTypes = commentTypes.joinToString(",") { it.name }
                        )
                    )
                }
            }
        }
    }

    fun continueConversation(
        chatId: String,
        userReply: String,
        imageUri: Uri? = null,
        language: Language,
        context: android.content.Context  // ✅ ADD CONTEXT PARAMETER
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val actualLanguage = _currentLanguage.value

            // ✅ GRANT PERSISTENT URI PERMISSION
            imageUri?.let { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    // Permission already granted or not available
                }
            }

            val userMessage = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                text = if (imageUri != null && userReply.isBlank()) "[Image]" else userReply,
                isUser = true,
                timestamp = System.currentTimeMillis(),
                imageUri = imageUri?.toString()
            )
            repository.saveMessage(userMessage)

            val messages = repository.getMessagesByChatId(chatId).first()
            val conversationHistory = messages.map {
                Message(it.id, it.text, it.isUser, it.timestamp)
            }

            val currentMood = _currentCommentTypes.value.map { it.name }

            val result = repository.continueConversationWithMood(
                chatId = chatId,
                userReply = userReply,
                imageUri = imageUri,
                conversationHistory = conversationHistory,
                language = actualLanguage,
                commentTypes = currentMood,
                userProfile = userProfile.value
            )

            result.fold(
                onSuccess = { aiReply ->
                    val aiMessage = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = aiReply,
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        imageUri = null
                    )
                    repository.saveMessage(aiMessage)

                    repository.getChatById(chatId).first()?.let { chat ->
                        repository.updateChat(
                            chat.copy(
                                lastMessage = aiReply,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }

                    _uiState.value = UiState.Success(aiReply, chatId)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }




    // ✅ Getter for UI access to current mood
    fun getCurrentCommentTypes(): StateFlow<List<CommentType>> = _currentCommentTypes

    // ------------------------------
    // Remaining unchanged functions
    // ------------------------------

    // Add this method to MainViewModel.kt
    fun regenerateLastResponse(chatId: String, messageId: String, language: Language) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Get all messages
            val allMessages = repository.getMessagesByChatId(chatId).first()

            // Find the AI message to regenerate
            val messageIndex = allMessages.indexOfFirst { it.id == messageId }
            if (messageIndex == -1) {
                _uiState.value = UiState.Error("Message not found")
                return@launch
            }

            // Get conversation history BEFORE this AI message
            val historyBeforeMessage = allMessages.take(messageIndex)

            // Find the last user message and its image
            val lastUserMessage = historyBeforeMessage.lastOrNull { it.isUser }
            val lastUserText = lastUserMessage?.text?.takeIf { it != "[Image]" } ?: ""
            val lastUserImageUri = lastUserMessage?.imageUri?.let { Uri.parse(it) }

            val conversationHistory = historyBeforeMessage.map {
                Message(it.id, it.text, it.isUser, it.timestamp)
            }

            // Use stored language and mood
            val actualLanguage = _currentLanguage.value
            val currentMood = _currentCommentTypes.value.map { it.name }

            // Regenerate using the last user's input (with image if present)
            val result = repository.continueConversationWithMood(
                chatId = chatId,
                userReply = lastUserText,
                imageUri = lastUserImageUri,
                conversationHistory = conversationHistory,
                language = actualLanguage,  // Use stored language
                commentTypes = currentMood,
                userProfile = userProfile.value
            )

            result.fold(
                onSuccess = { aiReply ->
                    // Save new regenerated AI response
                    val aiMessage = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = aiReply,
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        imageUri = null
                    )
                    repository.saveMessage(aiMessage)

                    // Update chat
                    repository.getChatById(chatId).first()?.let { chat ->
                        repository.updateChat(
                            chat.copy(
                                lastMessage = aiReply,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }

                    _uiState.value = UiState.Success(aiReply, chatId)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }


    fun retryGenerateComment() {
        val params = _lastGenerationParams.value ?: return
        val chatId = _currentChatId.value ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.generateComment(
                imageUri = params.imageUri,
                postText = params.postText,
                commentTypes = params.commentTypes,
                language = userProfile.value.preferredLanguage,
                userProfile = userProfile.value
            )
            result.fold(
                onSuccess = { comment ->
                    repository.getChatById(chatId).first()?.let { chat ->
                        repository.updateChat(chat.copy(lastMessage = comment, timestamp = System.currentTimeMillis()))
                    }
                    val message = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = comment,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.saveMessage(message)
                    _uiState.value = UiState.Success(comment, chatId)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun toggleFavorite(chat: ChatEntity) {
        viewModelScope.launch {
            repository.updateChat(
                chat.copy(isFavorite = !chat.isFavorite)
            )
        }
    }


    fun deleteChat(chat: ChatEntity) {
        viewModelScope.launch { repository.deleteChat(chat) }
    }

    fun deleteAllChats() {
        viewModelScope.launch { repository.deleteAllChats() }
    }

    fun updateLanguage(language: Language) {
        viewModelScope.launch { preferencesManager.updateLanguage(language) }
    }

    fun resetApp() {
        viewModelScope.launch {
            preferencesManager.resetOnboarding()
            repository.deleteAllChats()
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Retrying(val attempt: Int) : UiState()
        data class Success(val message: String, val chatId: String) : UiState()
        data class Error(val message: String) : UiState()
    }
}
