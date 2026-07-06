package com.iti.data.repositories

import com.iti.data.sources.local.onboarding.OnboardingLocalDataSource
import com.iti.domain.repositories.onboarding.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val localDataSource: OnboardingLocalDataSource
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return localDataSource.isOnboardingCompleted()
    }

    override suspend fun setOnboardingCompleted() {
        localDataSource.setOnboardingCompleted()
    }
}