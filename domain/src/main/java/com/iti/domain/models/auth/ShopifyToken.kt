package com.iti.domain.models.auth

data class ShopifyCustomerToken(
    val customerId: String?,
    val accessToken: String,
    val expiresAt: String
)