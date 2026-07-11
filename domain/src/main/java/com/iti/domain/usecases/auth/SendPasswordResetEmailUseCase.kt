package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository

class SendPasswordResetEmailUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = repository.sendPasswordResetEmail(email)
}