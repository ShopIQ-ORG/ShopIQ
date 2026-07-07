package com.iti.presentation.screens.ai.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.ChatMessage
import com.iti.domain.models.Result
import com.iti.domain.usecases.ai.ClearChatHistoryUseCase
import com.iti.domain.usecases.ai.GetChatHistoryUseCase
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AiHistoryViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiHistoryContract.State())
    val state: StateFlow<AiHistoryContract.State> = _state.asStateFlow()

    private val _effect = Channel<AiHistoryContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var historyJob: Job? = null
    private var allConversations: List<AiHistoryContract.ConversationItem> = emptyList()

    init {
        sendIntent(AiHistoryContract.Intent.LoadHistory)
    }

    fun sendIntent(intent: AiHistoryContract.Intent) {
        when (intent) {
            is AiHistoryContract.Intent.LoadHistory -> loadHistory()
            is AiHistoryContract.Intent.SearchQueryChanged -> handleSearch(intent.query)
            is AiHistoryContract.Intent.DeleteAllClicked -> {
                _state.update { it.copy(showDeleteConfirmDialog = true) }
            }
            is AiHistoryContract.Intent.DismissDeleteDialog -> {
                _state.update { it.copy(showDeleteConfirmDialog = false) }
            }
            is AiHistoryContract.Intent.ConfirmDeleteAll -> clearHistory()
            is AiHistoryContract.Intent.ConversationClicked -> {
                viewModelScope.launch {
                    com.iti.presentation.screens.ai.AiChatSharedState.scrollToMessageTimestamp.value = intent.item.timestamp
                    _effect.send(AiHistoryContract.Effect.NavigateBack)
                }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result !is Result.Success || result.data !is com.iti.domain.models.User.AuthenticatedUser) {
                _state.update { it.copy(isLoading = false, errorMessage = UiText.Plain("User not authenticated.")) }
                return@launch
            }
            val user = result.data as com.iti.domain.models.User.AuthenticatedUser

            historyJob?.cancel()
            historyJob = viewModelScope.launch {
                getChatHistoryUseCase(user.uid).collect { chatResult ->
                        when (chatResult) {
                            is Result.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
                            is Result.Success -> {
                                val items = pairMessagesIntoConversations(chatResult.data)
                                allConversations = items
                                filterConversations(_state.value.searchQuery)
                            }
                            is Result.Failure -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = UiText.Plain(chatResult.exception.message ?: "Unknown Error")
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun pairMessagesIntoConversations(messages: List<ChatMessage>): List<AiHistoryContract.ConversationItem> {
        val items = mutableListOf<AiHistoryContract.ConversationItem>()
        // Messages are ordered by timestamp ascending from the backend usually, but let's ensure it:
        val sortedMessages = messages.sortedByDescending { it.timestamp }
        
        // Let's do ascending for easier pairing
        val ascMessages = messages.sortedBy { it.timestamp }
        var j = 0
        while (j < ascMessages.size) {
            val msg = ascMessages[j]
            if (msg.sender == "user") {
                // Peek next to see if it's an AI message
                var aiSnippet = "..."
                if (j + 1 < ascMessages.size && ascMessages[j + 1].sender == "ai") {
                    aiSnippet = ascMessages[j + 1].text
                }
                
                items.add(
                    AiHistoryContract.ConversationItem(
                        id = UUID.randomUUID().toString(),
                        query = msg.text.ifBlank { if (msg.attachedImageUrl != null) "Image Search" else "Voice Search" },
                        aiResponseSnippet = if (aiSnippet.length > 60) aiSnippet.substring(0, 60) + "..." else aiSnippet,
                        timestamp = msg.timestamp
                    )
                )
            }
            j++
        }
        
        return items.sortedByDescending { it.timestamp }
    }

    private fun handleSearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterConversations(query)
    }

    private fun filterConversations(query: String) {
        val filtered = if (query.isBlank()) {
            allConversations
        } else {
            allConversations.filter {
                it.query.contains(query, ignoreCase = true) || it.aiResponseSnippet.contains(query, ignoreCase = true)
            }
        }
        _state.update {
            it.copy(
                isLoading = false,
                conversations = filtered
            )
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result !is Result.Success || result.data !is com.iti.domain.models.User.AuthenticatedUser) {
                return@launch
            }
            val user = result.data as com.iti.domain.models.User.AuthenticatedUser

            _state.update { it.copy(showDeleteConfirmDialog = false, isLoading = true) }
            clearChatHistoryUseCase(user.uid).collect { chatResult ->
                when (chatResult) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        _state.update { it.copy(isLoading = false, conversations = emptyList()) }
                        _effect.send(AiHistoryContract.Effect.ShowToast(UiText.Plain("History deleted")))
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isLoading = false) }
                        _effect.send(AiHistoryContract.Effect.ShowToast(UiText.Plain("Failed to delete history")))
                    }
                }
            }
        }
    }
}
