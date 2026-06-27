package com.iti.domain.usecases.auth

import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): User? =
        repository.getCurrentUser()
}