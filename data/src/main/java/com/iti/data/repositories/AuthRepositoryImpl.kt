package com.iti.data.repositories

import com.iti.data.core.handleException
import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): Result<User> {
        return try {
            Result.Success(remoteDataSource.login(credentials).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            Result.Success(remoteDataSource.loginWithGoogle(idToken).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun loginWithFacebook(accessToken: String): Result<User> {
        return try {
            Result.Success(remoteDataSource.loginWithFacebook(accessToken).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun loginAsGuest(): Result<User> {
        return try {
            Result.Success(remoteDataSource.loginAsGuest().toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun register(info: RegistrationInfo): Result<User> {
        return try {
            Result.Success(remoteDataSource.register(info).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            Result.Success(remoteDataSource.getCurrentUser().toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun validateAuthenticatedUser(): Result<Unit> {
        return when (val userResult = getCurrentUser()) {
            Result.Loading ->   Result.Loading
            is Result.Failure -> userResult
            is Result.Success -> when (userResult.data) {
                User.GuestUser -> Result.Failure(AuthException.UnauthorizedAccess())
                is User.AuthenticatedUser -> Result.Success(Unit)
            }
        }
    }

    override fun logout(): Result<Unit> {
        return try {
            remoteDataSource.logout()
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override fun getUserId(): String? = remoteDataSource.getUserId()

}