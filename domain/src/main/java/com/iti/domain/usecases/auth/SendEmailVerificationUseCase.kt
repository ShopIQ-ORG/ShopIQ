package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.repositories.auth.AuthRepository

class SendEmailVerificationUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.sendEmailVerification()
}