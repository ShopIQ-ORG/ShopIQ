package com.iti.domain.usecases.auth
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Result<User> =
        repository.getCurrentUser()
}