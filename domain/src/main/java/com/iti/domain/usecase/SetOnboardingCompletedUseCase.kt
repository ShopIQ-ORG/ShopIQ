package com.iti.domain.usecase

import com.iti.domain.repository.OnboardingRepository

class SetOnboardingCompletedUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}
