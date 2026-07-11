package com.iti.domain.usecases.auth

import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.user.UserRepository

class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fullName: String,
        phone: String,
        dateOfBirth: String?,
        gender: String?,
        avatarUrl: String?
    ): Result<User> {
        return userRepository.updateProfile(
            fullName = fullName,
            phone = phone,
            dateOfBirth = dateOfBirth,
            gender = gender,
            avatarUrl = avatarUrl
        )
    }
}
