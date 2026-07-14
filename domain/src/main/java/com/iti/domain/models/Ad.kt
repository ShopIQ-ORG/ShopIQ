package com.iti.domain.models

data class Ad(
    val id: String,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val arTitle: String? = null,
    val arSubtitle: String? = null
)
