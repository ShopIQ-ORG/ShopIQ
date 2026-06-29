package com.iti.domain.models.cart

import com.iti.domain.models.Money

data class Cart(
    val items: List<CartItem>,
    val subtotal: Money,
    val discount: Money?,
    val promoCode: String?,
    val shippingCost: Money?,
    val total: Money
)

