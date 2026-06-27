package com.iti.domain.exceptions

sealed class AppException : Exception() {
    class Unknown(cause: Throwable?) : AppException()
}
