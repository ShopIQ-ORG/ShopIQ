package com.iti.domain.usecases.onboarding

import com.iti.domain.repositories.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow

class IsOnboardingCompletedUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.isOnboardingCompleted()
    }
}
