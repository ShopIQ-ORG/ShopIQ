package com.iti.domain.exceptions

sealed class AuthException(message: String) : AppException(message) {
    class InvalidCredentials : AuthException("Invalid email or password")
    class UserNotFound : AuthException("User account not found")
    class EmailAlreadyInUse : AuthException("Email is already registered")
    class WeakPassword : AuthException("Password is too weak")

    class EmailNotVerified(val email: String) : AuthException("Email address is not verified")
    class ShopifyTokenUnavailable : AuthException("Unable to obtain a storefront access token")
    class UnauthorizedAccess : AuthException("You must be signed in to access this feature")
}