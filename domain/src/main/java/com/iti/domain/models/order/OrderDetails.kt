package com.iti.domain.models.order

data class OrderDetails(
    val id: String,
    val name: String,
    val createdAt: String,
    val financialStatus: String,
    val fulfillmentStatus: OrderStatus,
    val subtotalPrice: Double,
    val totalShippingPrice: Double,
    val totalPrice: Double,
    val totalDiscounts: Double,
    val currencyCode: String,
    val shippingAddress: ShippingAddress?,
    val lineItems: List<OrderLineItem>
)

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
    val variantTitle: String?,
    val price: Double,
    val imageUrl: String?
)