package com.iti.domain.repositories.ai

import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface ChatbotRepository {
    fun getChatHistory(userId: String): Flow<Result<List<ChatMessage>>>
    fun sendMessage(
        userId: String,
        userMessage: ChatMessage,
        imageBytes: ByteArray? = null,
        currencyContext: String? = null,
        exchangeRate: Double = 1.0
    ): Flow<Result<ChatMessage>>
    fun clearChatHistory(userId: String): Flow<Result<Unit>>
}
