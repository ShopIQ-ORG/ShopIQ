package com.iti.domain.repositories.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun loginWithFacebook(accessToken: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
    suspend fun register(info: RegistrationInfo): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun validateAuthenticatedUser(): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun reloadAndGetCurrentUser(): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun getUserId(): String?
}