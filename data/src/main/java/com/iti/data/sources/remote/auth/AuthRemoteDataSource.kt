package com.iti.data.sources.remote.auth

import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo

interface AuthRemoteDataSource {
    suspend fun login(credentials: LoginCredentials): UserDto
    suspend fun register(info: RegistrationInfo): UserDto
    suspend fun getCurrentUser(): UserDto
    fun logout()
}
