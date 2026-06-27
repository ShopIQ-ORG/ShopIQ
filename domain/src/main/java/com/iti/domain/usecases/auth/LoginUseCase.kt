package com.iti.domain.usecases.auth

import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.repositories.auth.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(params: LoginCredentials): Result<User> =
        repository.login(params)
}