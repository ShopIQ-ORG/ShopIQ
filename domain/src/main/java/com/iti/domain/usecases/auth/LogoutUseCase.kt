package com.iti.domain.usecases.auth

import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.models.Result

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() =
        repository.logout()
}