package com.iti.domain.usecases.ai

import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Result
import com.iti.domain.repositories.ai.ChatbotRepository
import kotlinx.coroutines.flow.Flow

class SendChatMessageUseCase(private val repository: ChatbotRepository) {
    operator fun invoke(
        userId: String,
        userMessage: ChatMessage,
        imageBytes: ByteArray? = null,
        currencyContext: String? = null,
        exchangeRate: Double = 1.0
    ): Flow<Result<ChatMessage>> {
        return repository.sendMessage(userId, userMessage, imageBytes, currencyContext, exchangeRate)
    }
}
