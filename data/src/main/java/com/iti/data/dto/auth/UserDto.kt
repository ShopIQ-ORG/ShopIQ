package com.iti.data.dto.auth

data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val avatarUrl: String? = null,
    val isGuest: Boolean = false,
    val shopifyCustomerId: String? = null,
    val shopifyAccessToken: String? = null,
    val shopifyTokenExpiresAt: String? = null,
    val shopifyPassword: String? = null
)