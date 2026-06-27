package com.iti.data.dto.auth

import com.iti.domain.models.User

data class UserDto(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = ""
)

