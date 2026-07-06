package com.iti.data.dto.orders

data class OrderDto(
    val id: String,
    val name: String,
    val processedAt: String,
    val financialStatus: String,
    val fulfillmentStatus: String,
    val subtotalPrice: MoneyDto,
    val totalShippingPrice: MoneyDto,
    val totalPrice: MoneyDto,
    val totalRefunded: MoneyDto,
    val totalTax: MoneyDto,
    val shippingAddress: ShippingAddressDto?,
    val lineItems: List<OrderLineItemDto>
)

data class MoneyDto(
    val amount: String,
    val currencyCode: String
)

data class ShippingAddressDto(
    val firstName: String?,
    val lastName: String?,
    val address1: String?,
    val city: String?,
    val country: String?,
    val zip: String?
)

data class OrderLineItemDto(
    val title: String,
    val quantity: Int,
    val currentQuantity: Int,
    val originalTotalPrice: MoneyDto,
    val discountedTotalPrice: MoneyDto,
    val variant: OrderLineItemVariantDto?
)

data class OrderLineItemVariantDto(
    val id: String,
    val title: String,
    val sku: String?,
    val price: MoneyDto,
    val imageUrl: String?,
    val productId: String,
    val productTitle: String,
    val productHandle: String
)