package com.iti.domain.usecases.auth

import android.util.Log
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.models.Result
import com.iti.domain.util.CacheInvalidator

import com.iti.domain.models.User

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val cacheInvalidator: CacheInvalidator
) {

    suspend operator fun invoke(): Result<Unit> {
        val currentUserResult = authRepository.getCurrentUser()
        
        if (currentUserResult is Result.Success && currentUserResult.data is User.GuestUser) {
            cacheInvalidator.invalidate()
            return Result.Success(Unit)
        }

        return when (val result = authRepository.logout()) {
            is Result.Success -> {
                cacheInvalidator.invalidate()
                result
            }

            is Result.Failure -> result
            is Result.Loading -> result
        }
    }
}