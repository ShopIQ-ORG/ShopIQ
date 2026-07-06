package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository

class ReloadAndGetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> = authRepository.reloadAndGetCurrentUser()
}