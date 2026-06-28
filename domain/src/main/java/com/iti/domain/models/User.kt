package com.iti.domain.models

sealed class User {
    data class AuthenticatedUser(
        val uid: String,
        val fullName: String,
        val email: String,
        val phone: String
    ) : User()

    object GuestUser : User()
}