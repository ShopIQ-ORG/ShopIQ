package com.iti.domain.usecases.auth

import android.util.Log
import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository
import com.iti.domain.util.CacheInvalidator

import com.iti.domain.models.User

class LogoutUseCase(
    private val userRepository: UserRepository,
    private val cacheInvalidator: CacheInvalidator
) {

    suspend operator fun invoke(): Result<Unit> {
        val currentUserResult = userRepository.getCurrentUser()
        
        if (currentUserResult is Result.Success && currentUserResult.data is User.GuestUser) {
            cacheInvalidator.invalidate()
            return Result.Success(Unit)
        }

        return when (val result = userRepository.logout()) {
            is Result.Success -> {
                cacheInvalidator.invalidate()
                result
            }

            is Result.Failure -> result
            is Result.Loading -> result
        }
    }
}