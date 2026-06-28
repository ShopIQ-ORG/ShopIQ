package com.iti.domain.models

data class Category(
    val id: String,
    val title: String,
    val handle: String,
    val imageUrl: String?,
    val productsCount: Int
)
