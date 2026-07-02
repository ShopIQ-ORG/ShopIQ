package com.iti.domain.models.cart

import com.iti.domain.models.Money

private const val LOW_STOCK_THRESHOLD = 5

data class CartItem(
    val id: String,
    val productId: String,
    val variantId: String,
    val title: String,
    val variant: String,
    val price: Money,
    val imageUrl: String,
    val quantity: Int,
    val isAvailableForSale: Boolean,
    val quantityAvailable: Int
)


val CartItem.isLowStock: Boolean
    get() = isAvailableForSale &&
            quantityAvailable != 0 &&
            quantityAvailable in 1 until LOW_STOCK_THRESHOLD

val CartItem.hasKnownInventoryLimit: Boolean
    get() = quantityAvailable > 0

val CartItem.atMaxQuantity: Boolean
    get() = hasKnownInventoryLimit &&
            quantity >= quantityAvailable