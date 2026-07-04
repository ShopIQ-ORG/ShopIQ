package com.iti.domain.models

data class ChatMessage(
    val id: String = "",
    val sender: String = "", // "user" or "ai"
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val recommendedProductIds: List<String> = emptyList(),
    val voiceDuration: String? = null,
    val attachedImageUrl: String? = null
)
