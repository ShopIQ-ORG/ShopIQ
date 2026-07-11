package com.iti.domain.usecases.auth
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.repositories.user.UserRepository

class RegisterUseCase(
    private val repository: UserRepository
) {
     suspend operator fun invoke(params: RegistrationInfo): Result<User> =
        repository.register(params)
}