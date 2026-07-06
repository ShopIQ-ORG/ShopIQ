package com.iti.domain.models

import com.iti.domain.models.auth.AuthProvider

sealed class User {
    data class AuthenticatedUser(
        val uid: String,
        val fullName: String,
        val email: String,
        val phone: String,
        val provider: AuthProvider,
        val isEmailVerified: Boolean
    ) : User()

    object GuestUser : User()
}