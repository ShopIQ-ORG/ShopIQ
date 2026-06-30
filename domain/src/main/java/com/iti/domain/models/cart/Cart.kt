package com.iti.domain.models.cart

import com.iti.domain.models.Money

data class Cart(
    val id: String,
    val items: List<CartItem>,
    val discountCodes: List<String>,
    val subtotal: Money,
    val total: Money,
    val totalTax: Money?
)

