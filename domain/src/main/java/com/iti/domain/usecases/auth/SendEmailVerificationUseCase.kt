package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository

class SendEmailVerificationUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.sendEmailVerification()
}