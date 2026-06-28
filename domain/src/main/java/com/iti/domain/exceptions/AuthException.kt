package com.iti.domain.exceptions

sealed class AuthException : AppException() {
    class InvalidCredentials : AuthException()
    class UserNotFound : AuthException()
    class EmailAlreadyInUse : AuthException()
    class WeakPassword : AuthException()
}