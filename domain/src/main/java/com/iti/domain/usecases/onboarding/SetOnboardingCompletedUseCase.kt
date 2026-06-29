package com.iti.domain.usecases.onboarding

import com.iti.domain.repositories.onboarding.OnboardingRepository

class SetOnboardingCompletedUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}
