package com.iti.domain.usecases.ai

import com.iti.domain.models.Result
import com.iti.domain.repositories.ai.ChatbotRepository
import kotlinx.coroutines.flow.Flow

class ClearChatHistoryUseCase(
    private val repository: ChatbotRepository
) {
    operator fun invoke(userId: String): Flow<Result<Unit>> {
        return repository.clearChatHistory(userId)
    }
}
