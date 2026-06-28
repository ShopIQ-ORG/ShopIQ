package com.iti.domain.exceptions

sealed class NetworkException : AppException() {
    class NoConnection : NetworkException()
    data class ServerError(val code: Int, override val message: String?) : NetworkException()
}