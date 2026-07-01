package com.iti.domain.usecases.auth

import android.util.Log
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.models.Result
import com.iti.domain.util.CacheInvalidator

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val cacheInvalidator: CacheInvalidator
) {

    suspend operator fun invoke(): Result<Unit> {
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