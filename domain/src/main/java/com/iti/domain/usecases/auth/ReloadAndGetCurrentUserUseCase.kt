package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.user.UserRepository

class ReloadAndGetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = userRepository.reloadAndGetCurrentUser()
}