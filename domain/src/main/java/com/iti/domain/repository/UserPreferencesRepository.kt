package com.iti.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    fun getUserToken(): Flow<String?>
    suspend fun saveUserToken(token: String?)

    fun isDarkModeEnabled(): Flow<Boolean>
    suspend fun setDarkModeEnabled(enabled: Boolean)

    suspend fun clearAllPreferences()
}
