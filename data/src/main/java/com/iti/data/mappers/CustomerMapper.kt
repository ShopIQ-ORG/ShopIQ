package com.iti.data.mappers
import com.iti.data.dto.shopifycustomer.ShopifyCustomerTokenDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.domain.models.auth.ShopifyCustomerToken

fun ShopifyCustomerTokenDto.toDomain() = ShopifyCustomerToken(
    customerId = customerId,
    accessToken = accessToken,
    expiresAt = expiresAt
)


fun ShopifyFieldsDto.toDomain() = ShopifyCustomerToken(customerId, accessToken, expiresAt)