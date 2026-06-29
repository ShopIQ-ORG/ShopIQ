package com.iti.domain.models.cart

import com.iti.domain.models.Money

data class CartItem(
    val id: String,
    val productId: String,
    val title: String,
    val variant: String,
    val price: Money,
    val imageUrl: String,
    val quantity: Int
)