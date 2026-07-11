package com.iti.domain.usecases.auth

import com.iti.domain.repositories.user.UserRepository

class IsGuestUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Boolean {
        return userRepository.isGuest()
    }
}
