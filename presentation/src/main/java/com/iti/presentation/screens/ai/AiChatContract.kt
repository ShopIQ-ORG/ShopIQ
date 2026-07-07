package com.iti.presentation.screens.ai

import com.iti.domain.models.Product
import com.iti.domain.models.User

object AiChatContract {

    data class ChatProductUi(
        val id: String,
        val name: String,
        val price: String,
        val imageUrl: String,
        val stockStatus: String,
        val details: String
    )

    data class ChatMessageUi(
        val id: String,
        val sender: String, // "user" or "ai"
        val text: String,
        val timestamp: Long,
        val recommendedProducts: List<ChatProductUi> = emptyList(),
        val voiceDuration: String? = null,
        val attachedImageUrl: String? = null,
        val isResolvingProducts: Boolean = false
    )

    data class State(
        val messages: List<ChatMessageUi> = emptyList(),
        val isLoading: Boolean = false,
        val isBotTyping: Boolean = false,
        val error: String? = null,
        val currentUser: User? = null
    )

    sealed class Intent {
        data class SetUser(val user: User?) : Intent()
        data class SendMessage(
            val text: String,
            val imageBytes: ByteArray? = null,
            val attachedImageUrl: String? = null,
            val voiceDuration: String? = null
        ) : Intent()
        data object ClearChat : Intent()
    }
}
