package com.iti.data.dto.auth

data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val isGuest: Boolean = false
)

