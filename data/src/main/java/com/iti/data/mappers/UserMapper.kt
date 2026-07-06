package com.iti.data.mappers
import com.google.firebase.auth.FirebaseUser
import com.iti.data.dto.auth.FirebaseUserInfo
import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.domain.models.User
import com.iti.domain.models.auth.AuthProvider


fun UserDto.toDomain(
    provider: AuthProvider = AuthProvider.PASSWORD,
    isEmailVerified: Boolean = false
): User {
    if (isGuest) return User.GuestUser
    return User.AuthenticatedUser(
        uid = id,
        fullName = fullName,
        email = email,
        phone = phone,
        provider = provider,
        isEmailVerified = isEmailVerified
    )
}

fun UserDto.applyShopifyFields(fields: ShopifyFieldsDto) = copy(
    shopifyCustomerId = fields.customerId,
    shopifyAccessToken = fields.accessToken,
    shopifyTokenExpiresAt = fields.expiresAt,
    shopifyPassword = fields.password ?: shopifyPassword
)

fun FirebaseUser.toFirebaseUserInfo() = FirebaseUserInfo(
    uid = uid,
    isAnonymous = isAnonymous,
    displayName = displayName,
    email = email,
    isEmailVerified = isEmailVerified,
    providerIds = providerData.map { it.providerId }
)