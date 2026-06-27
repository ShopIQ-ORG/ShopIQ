package com.iti.domain.repositories.auth

import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun register(info: RegistrationInfo): Result<User>
    fun getCurrentUser(): User?
    fun logout()
}