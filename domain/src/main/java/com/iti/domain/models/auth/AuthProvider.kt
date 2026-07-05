package com.iti.domain.models.auth

enum class AuthProvider {
    PASSWORD,
    GOOGLE,
    GUEST,
    UNKNOWN;

    companion object {
        fun fromProviderIds(providerIds: List<String>): AuthProvider = when {
            providerIds.contains("google.com") -> GOOGLE
            providerIds.contains("password") -> PASSWORD
            providerIds.isEmpty() -> GUEST
            else -> UNKNOWN
        }
    }
}