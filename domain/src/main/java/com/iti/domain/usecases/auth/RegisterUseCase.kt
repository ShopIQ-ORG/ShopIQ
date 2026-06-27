package com.iti.domain.usecases.auth
import com.iti.domain.models.User
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.repositories.auth.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
     suspend operator fun invoke(params: RegistrationInfo): Result<User> =
        repository.register(params)
}