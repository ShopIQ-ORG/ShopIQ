package com.iti.domain.repositories.user

import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.PlaceSuggestion
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun loginWithFacebook(accessToken: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
    suspend fun register(info: RegistrationInfo): Result<User>
    suspend fun getCurrentUser(): Result<User>
    suspend fun validateAuthenticatedUser(): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun reloadAndGetCurrentUser(): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun getUserId(): String?
    fun isGuest(): Boolean
    suspend fun updateProfile(fullName: String, phone: String, dateOfBirth: String?, gender: String?, avatarUrl: String?): Result<User>

    fun getSavedAddresses(): Flow<Result<List<Address>>>
    suspend fun saveAddress(address: Address): Result<Unit>
    suspend fun deleteAddress(addressId: String): Result<Unit>
    suspend fun getPlaceSuggestions(query: String, apiKey: String): Result<List<PlaceSuggestion>>
    suspend fun searchLocationByName(query: String, apiKey: String): Result<LocationCoordinates?>
}
