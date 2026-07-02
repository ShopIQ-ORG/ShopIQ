package com.iti.data.dto.shopifycustomer

data class ShopifyFieldsDto(
    val customerId: String?,
    val accessToken: String,
    val expiresAt: String,
    val password: String?
)