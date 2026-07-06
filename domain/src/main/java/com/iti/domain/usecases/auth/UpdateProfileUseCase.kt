package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository

class UpdateProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        phone: String,
        dateOfBirth: String?,
        gender: String?,
        avatarUrl: String?
    ): Result<User> {
        return authRepository.updateProfile(
            fullName = fullName,
            phone = phone,
            dateOfBirth = dateOfBirth,
            gender = gender,
            avatarUrl = avatarUrl
        )
    }
}
