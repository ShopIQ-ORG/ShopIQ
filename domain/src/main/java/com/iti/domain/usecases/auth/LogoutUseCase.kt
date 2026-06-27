package com.iti.domain.usecases.auth

import com.iti.domain.repositories.auth.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() =
        repository.logout()
}