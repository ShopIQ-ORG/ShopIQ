package com.iti.domain.usecases.onboarding

import com.iti.domain.repositories.settings.SettingsRepository

class SetOnboardingCompletedUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}
