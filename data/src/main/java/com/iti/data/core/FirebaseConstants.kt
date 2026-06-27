package com.iti.data.core

object FirebaseConstants {

    object Collections {
        const val USERS = "users"
    }

    object AuthErrors {
        const val WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
        const val INVALID_CREDENTIAL = "ERROR_INVALID_CREDENTIAL"
        const val USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
        const val WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
    }
}