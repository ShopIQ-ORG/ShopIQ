package com.iti.data.mappers
import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.domain.models.User

fun UserDto.toDomain(): User {
    return if (isGuest) {
        User.GuestUser
    } else {
        User.AuthenticatedUser(
            uid = id,
            fullName = fullName,
            email = email,
            phone = phone
        )
    }
}

fun UserDto.applyShopifyFields(fields: ShopifyFieldsDto) = copy(
    shopifyCustomerId = fields.customerId,
    shopifyAccessToken = fields.accessToken,
    shopifyTokenExpiresAt = fields.expiresAt,
    shopifyPassword = fields.password ?: shopifyPassword
)