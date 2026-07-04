package com.iti.presentation.screens.ai.history

import com.iti.presentation.util.UiText

object AiHistoryContract {
    data class ConversationItem(
        val id: String,
        val query: String,
        val aiResponseSnippet: String,
        val timestamp: Long
    )

    data class State(
        val isLoading: Boolean = true,
        val conversations: List<ConversationItem> = emptyList(),
        val searchQuery: String = "",
        val errorMessage: UiText? = null,
        val showDeleteConfirmDialog: Boolean = false
    )

    sealed class Intent {
        data class SearchQueryChanged(val query: String) : Intent()
        object DeleteAllClicked : Intent()
        object ConfirmDeleteAll : Intent()
        object DismissDeleteDialog : Intent()
        object LoadHistory : Intent()
        data class ConversationClicked(val item: ConversationItem) : Intent()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowToast(val message: UiText) : Effect()
    }
}
