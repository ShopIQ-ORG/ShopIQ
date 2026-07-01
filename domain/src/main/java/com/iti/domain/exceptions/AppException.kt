package com.iti.domain.exceptions

sealed class AppException(message: String) : Exception(message) {
    class Unknown(cause: Throwable?) : AppException("An unknown error occurred: ${cause?.message}")
}
