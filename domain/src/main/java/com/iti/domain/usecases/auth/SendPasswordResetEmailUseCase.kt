package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.repositories.auth.AuthRepository

class SendPasswordResetEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = repository.sendPasswordResetEmail(email)
}