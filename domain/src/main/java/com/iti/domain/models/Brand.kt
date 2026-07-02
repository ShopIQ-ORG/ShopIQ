package com.iti.domain.models

data class Brand(
    val id: String,
    val name: String,
    val imageUrl: String,
    val mappedImageUrl: String = ""
)
