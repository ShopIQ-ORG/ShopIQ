package com.iti.domain.models.auth

data class RegistrationInfo(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)