package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository

class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<User> =
        repository.loginWithGoogle(idToken)
}