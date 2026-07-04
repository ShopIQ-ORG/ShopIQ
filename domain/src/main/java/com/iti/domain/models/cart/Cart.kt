package com.iti.domain.models.cart

import com.iti.domain.models.Money

data class Cart(
    val id: String,
    val checkoutUrl: String?,
    val items: List<CartItem>,
    val discountCodes: List<String>,
    val discountAmount: Money?,
    val subtotal: Money,
    val total: Money,
    val totalTax: Money?,
    val shippingAmount: Money?,
    val buyerIdentity: CartBuyerIdentity? = null
) {
    val hasOutOfStockItems: Boolean
        get() = items.any { !it.isAvailableForSale }

    val appliedPromoCode: String?
        get() = discountCodes.firstOrNull()
}

data class CartBuyerIdentity(
    val email: String?,
    val phone: String?,
    val countryCode: String?
)

fun Cart.recalculatedAfterQuantityChange(): Cart {
    val newSubtotal = items.sumOf { item ->
        (item.price.amount.toDoubleOrNull() ?: 0.0) * item.quantity
    }

    val discountValue = discountAmount?.amount?.toDoubleOrNull() ?: 0.0
    val taxValue = totalTax?.amount?.toDoubleOrNull() ?: 0.0
    val shippingValue = shippingAmount?.amount?.toDoubleOrNull() ?: 0.0
    val newTotal = (newSubtotal - discountValue + taxValue + shippingValue).coerceAtLeast(0.0)

    val currency = subtotal.currencyCode

    return copy(
        subtotal = Money("%.2f".format(newSubtotal), currency),
        total = Money("%.2f".format(newTotal), currency)
    )
}
