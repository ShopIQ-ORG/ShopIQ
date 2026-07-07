package com.iti.presentation.screens.ai

import kotlinx.coroutines.flow.MutableStateFlow

object AiChatSharedState {
    val scrollToMessageTimestamp = MutableStateFlow<Long?>(null)
}