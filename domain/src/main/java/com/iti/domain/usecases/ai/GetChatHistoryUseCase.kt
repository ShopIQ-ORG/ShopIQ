package com.iti.domain.usecases.ai

import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Result
import com.iti.domain.repositories.ai.ChatbotRepository
import kotlinx.coroutines.flow.Flow

class GetChatHistoryUseCase(private val repository: ChatbotRepository) {
    operator fun invoke(userId: String): Flow<Result<List<ChatMessage>>> {
        return repository.getChatHistory(userId)
    }
}
