package com.iti.data.dto.shopifycustomer

data class ShopifyCustomerDto(
    val id: String,
    val email: String?
)

data class ShopifyCustomerTokenDto(
    val customerId: String?,
    val accessToken: String,
    val expiresAt: String
)