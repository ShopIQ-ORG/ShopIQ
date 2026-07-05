package com.iti.presentation.util

import com.iti.domain.models.Product
import com.iti.domain.models.Money

val Product.discountPercent: Int
    get() = when (this.id.substringAfterLast("/").toLongOrNull() ?: 0L) {
        9746396905707L -> 25 // Nike Air Force
        9746397135083L -> 30 // Adidas Hoodie
        9746397298923L -> 27 // Vans Cap
        else -> {
            val idNum = this.id.substringAfterLast("/").toLongOrNull() ?: 0L
            // Give discount if id is divisible by 3 (makes it look varied but deterministic)
            if (idNum % 3L == 0L) {
                15 + (idNum % 21).toInt() // 15% to 35% discount
            } else 0
        }
    }

val Product.hasDiscount: Boolean
    get() = discountPercent > 0

val Product.compareAtPrice: Money?
    get() {
        val pct = discountPercent
        if (pct <= 0) return null
        val currentPrice = minPrice.amount.toDoubleOrNull() ?: return null
        val originalPrice = currentPrice / (1.0 - (pct / 100.0))
        return Money(
            amount = "%.0f".format(originalPrice),
            currencyCode = minPrice.currencyCode
        )
    }
