package com.iti.domain.models.order

data class Order(
    val id: String,
    val name: String,
    val createdAt: String,
    val financialStatus: OrderFinancialStatus,
    val fulfillmentStatus: OrderStatus,
    val subtotalPrice: Money,
    val totalShippingPrice: Money,
    val totalPrice: Money,
    val totalRefunded: Money,
    val totalTax: Money,
    val shippingAddress: ShippingAddress?,
    val lineItems: List<OrderLineItem>
) {
    val itemsCount: Int
        get() = lineItems.sumOf { it.quantity }

    val originalSubtotal: Money
        get() = Money(
            amount = lineItems.sumOf { it.originalTotalPrice.amount },
            currencyCode = subtotalPrice.currencyCode
        )

    val totalDiscount: Money
        get() = Money(
            amount = (originalSubtotal.amount - subtotalPrice.amount).coerceAtLeast(0.0),
            currencyCode = subtotalPrice.currencyCode
        )
}

data class Money(
    val amount: Double,
    val currencyCode: String
)

enum class OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED, UNKNOWN
}

enum class OrderFinancialStatus {
    PENDING, AUTHORIZED, PARTIALLY_PAID, PAID, PARTIALLY_REFUNDED, REFUNDED, VOIDED, EXPIRED, UNKNOWN
}

data class ShippingAddress(
    val firstName: String?,
    val lastName: String?,
    val address1: String?,
    val city: String?,
    val country: String?,
    val zip: String?
)

data class OrderLineItem(
    val title: String,
    val quantity: Int,
    val currentQuantity: Int,
    val originalTotalPrice: Money,
    val discountedTotalPrice: Money,
    val variant: OrderLineItemVariant?
)

data class OrderLineItemVariant(
    val id: String,
    val title: String,
    val sku: String?,
    val price: Money,
    val imageUrl: String?,
    val productId: String,
    val productTitle: String,
    val productHandle: String
)