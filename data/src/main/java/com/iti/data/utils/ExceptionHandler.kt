package com.iti.data.utils

import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.iti.data.BuildConfig
import com.iti.domain.exceptions.AppException
import com.iti.domain.exceptions.AuthException
import com.iti.domain.exceptions.NetworkException
import java.io.IOException
import android.util.Log
import com.iti.domain.exceptions.CartException

fun Throwable.handleException(): Throwable {
    val result = when (this) {
        is FirebaseAuthWeakPasswordException -> AuthException.WeakPassword()
        is FirebaseAuthInvalidCredentialsException -> AuthException.InvalidCredentials()
        is FirebaseAuthInvalidUserException -> AuthException.UserNotFound()
        is FirebaseAuthUserCollisionException -> AuthException.EmailAlreadyInUse()
        is FirebaseNetworkException -> NetworkException.NoConnection()
        is ApolloNetworkException -> NetworkException.NoConnection()
        is ApolloHttpException -> NetworkException.ServerError(statusCode, message)
        is IOException -> NetworkException.NoConnection()
        is CartException -> this
        is AuthException -> this
        is NetworkException -> this
        is AppException -> this
        else -> AppException.Unknown(cause = this)
    }
    
    if (BuildConfig.DEBUG) {
        Log.e("ExceptionHandler", "Exception handled: ${result.message}", this)
    }
    
    return result
}
