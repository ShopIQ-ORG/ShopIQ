package com.iti.domain.exceptions

sealed class AppException(message: String) : Exception(message) {
    class Unknown(cause: Throwable?) : AppException("An unknown error occurred: ${cause?.message}")
    class TooManyRequests : AppException(
        "Too many attempts. Please wait a few minutes before trying again."
    )}
