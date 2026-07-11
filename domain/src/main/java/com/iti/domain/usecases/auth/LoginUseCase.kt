package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.repositories.user.UserRepository

class LoginUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(params: LoginCredentials): Result<User> =
        repository.login(params)
}