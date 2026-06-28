package com.iti.domain.usecase

import com.iti.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class IsOnboardingCompletedUseCase(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isOnboardingCompleted()
    }
}
