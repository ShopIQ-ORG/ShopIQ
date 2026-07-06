package com.iti.data.sources.local.onboarding

import kotlinx.coroutines.flow.Flow

interface OnboardingLocalDataSource {
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
