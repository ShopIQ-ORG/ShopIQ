package com.iti.domain.usecases.onboarding

import com.iti.domain.repositories.onboarding.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class IsOnboardingCompletedUseCase(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isOnboardingCompleted()
    }
}
