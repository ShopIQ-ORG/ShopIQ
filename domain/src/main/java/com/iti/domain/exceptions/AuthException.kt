package com.iti.domain.exceptions

sealed class AuthException(message: String) : AppException(message) {
    class InvalidCredentials : AuthException("Invalid email or password")
    class UserNotFound : AuthException("User account not found")
    class EmailAlreadyInUse : AuthException("Email is already registered")
    class WeakPassword : AuthException("Password is too weak")

    class UnidentifiedCustomer : AuthException("Unidentified customer")
    class UnauthorizedAccess : AuthException("You must be signed in to access this feature")}