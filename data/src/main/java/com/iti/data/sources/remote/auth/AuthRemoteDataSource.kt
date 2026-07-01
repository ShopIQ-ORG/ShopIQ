package com.iti.data.sources.remote.auth

import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo

interface AuthRemoteDataSource {
    suspend fun login(credentials: LoginCredentials): UserDto
    suspend fun loginWithGoogle(idToken: String): UserDto
    suspend fun loginWithFacebook(accessToken: String): UserDto
    suspend fun loginAsGuest(): UserDto
    suspend fun register(info: RegistrationInfo): UserDto
    suspend fun getCurrentUser(): UserDto
    fun getUserId(): String?
    fun logout()
}
