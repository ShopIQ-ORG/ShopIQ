package com.iti.data.repositories

import com.iti.data.core.handleException
import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
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

    override fun logout() = remoteDataSource.logout()
}