package com.iti.domain.repositories.auth

import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.models.Result

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun loginWithFacebook(accessToken: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
    suspend fun register(info: RegistrationInfo): Result<User>
    suspend fun getCurrentUser(): Result<User>
    fun getUserId(): String?
    fun logout()
}