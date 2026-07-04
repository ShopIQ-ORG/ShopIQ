package com.iti.domain.models.order

data class Order(
    val id: String,
    val name: String,
    val createdAt: String,
    val totalPrice: Double,
    val currencyCode: String,
    val fulfillmentStatus: OrderStatus,
    val itemsCount: Int
)

enum class OrderStatus {
    PENDING, PROCESSING, COMPLETED, CANCELLED, UNKNOWN
}