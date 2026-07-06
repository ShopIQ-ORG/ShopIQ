package com.iti.data.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.iti.data.utils.handleException
import com.iti.data.dto.auth.CredentialAuthResult
import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.data.mappers.applyShopifyFields
import com.iti.data.mappers.toDomain
import com.iti.data.mappers.toUserDto
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSource
import com.iti.data.sources.remote.user.UserRemoteDataSource
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.AuthProvider
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.models.auth.ShopifyCustomerToken
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.util.ShopifyTokenProvider
import kotlinx.coroutines.CancellationException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class AuthRepositoryImpl(
    private val authRemote: AuthRemoteDataSource,
    private val userRemote: UserRemoteDataSource,
    private val shopifyRemote: ShopifyCustomerRemoteDataSource,
    private val shopifyLocal: ShopifyTokenLocalDataSource? = null
) : AuthRepository, ShopifyTokenProvider {

    override suspend fun login(credentials: LoginCredentials): Result<User> = safeCall {
        authRemote.signInWithEmail(credentials.email, credentials.password)
        val firebaseUser = authRemote.getCurrentFirebaseUser() ?: throw AuthException.UserNotFound()

        if (!firebaseUser.isEmailVerified) {
            throw AuthException.EmailNotVerified(firebaseUser.email.orEmpty())
        }

        val userDto = userRemote.getUser(firebaseUser.uid)
        val refreshed = ensureShopifyToken(userDto)

        refreshed.toDomain(
            provider = AuthProvider.fromProviderIds(firebaseUser.providerIds),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> = safeCall {
        linkOrSignIn(GoogleAuthProvider.getCredential(idToken, null))
    }

    override suspend fun loginWithFacebook(accessToken: String): Result<User> = safeCall {
        linkOrSignIn(FacebookAuthProvider.getCredential(accessToken))
    }

    override suspend fun loginAsGuest(): Result<User> = safeCall {
        val uid = authRemote.signInAnonymously()
        val userDto = UserDto(id = uid, isGuest = true)
        userRemote.saveUser(uid, userDto, merge = true)
        userDto.toDomain()
    }

    override suspend fun register(info: RegistrationInfo): Result<User> = safeCall {
        val currentUser = authRemote.getCurrentFirebaseUser()

        val (uid, merge) = if (currentUser != null && currentUser.isAnonymous) {
            try {
                val result = authRemote.linkCurrentUserWithCredential(
                    EmailAuthProvider.getCredential(info.email, info.password)
                )
                result.uid to true
            } catch (_: FirebaseAuthUserCollisionException) {
                throw AuthException.EmailAlreadyInUse()
            }
        } else {
            authRemote.createUserWithEmail(info.email, info.password) to false
        }

        val baseUserDto = info.toUserDto(uid).copy(isGuest = false)
        val mockPassword = UUID.randomUUID().toString()
        val shopifyFields = provisionShopifyCustomer(
            email = info.email,
            fullName = info.fullName,
            password = mockPassword
        )
        val userDto = baseUserDto.applyShopifyFields(shopifyFields)
        userRemote.saveUser(uid, userDto, merge = merge)

        val firebaseUser = authRemote.getCurrentFirebaseUser() ?: throw AuthException.UserNotFound()
        userDto.toDomain(
            provider = AuthProvider.fromProviderIds(firebaseUser.providerIds),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }

    override suspend fun getCurrentUser(): Result<User> = safeCall {
        val firebaseUser = authRemote.getCurrentFirebaseUser() ?: throw AuthException.UserNotFound()
        if (firebaseUser.isAnonymous) return@safeCall UserDto(
            id = firebaseUser.uid,
            isGuest = true
        ).toDomain()

        val userDto = userRemote.getUserOrNull(firebaseUser.uid) ?: UserDto(
            id = firebaseUser.uid,
            fullName = firebaseUser.displayName.orEmpty(),
            email = firebaseUser.email.orEmpty(),
            isGuest = false
        )
        userDto.toDomain(
            provider = AuthProvider.fromProviderIds(firebaseUser.providerIds),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }

    override suspend fun validateAuthenticatedUser(): Result<Unit> {
        return when (val userResult = getCurrentUser()) {
            Result.Loading -> Result.Loading
            is Result.Failure -> userResult
            is Result.Success -> when (userResult.data) {
                User.GuestUser -> Result.Failure(AuthException.UnauthorizedAccess())
                is User.AuthenticatedUser -> Result.Success(Unit)
            }
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> = safeCall {
        authRemote.sendEmailVerification()
    }

    override suspend fun reloadAndGetCurrentUser(): Result<User> = safeCall {
        val firebaseUser = authRemote.reloadCurrentUser() ?: throw AuthException.UserNotFound()
        if (firebaseUser.isAnonymous) return@safeCall UserDto(
            id = firebaseUser.uid,
            isGuest = true
        ).toDomain()

        val userDto = userRemote.getUserOrNull(firebaseUser.uid) ?: UserDto(
            id = firebaseUser.uid,
            fullName = firebaseUser.displayName.orEmpty(),
            email = firebaseUser.email.orEmpty(),
            isGuest = false
        )
        userDto.toDomain(
            provider = AuthProvider.fromProviderIds(firebaseUser.providerIds),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = safeCall {
        authRemote.sendPasswordResetEmail(email)
    }

    override suspend fun logout(): Result<Unit> = safeCall {
        authRemote.signOut()
        shopifyLocal?.clear()
    }

    override fun getUserId(): String? = authRemote.getCurrentFirebaseUser()?.uid

    override suspend fun getValidToken(): Result<ShopifyCustomerToken> = safeCall {
        shopifyLocal?.getCachedFields()?.let { cached ->
            if (!isExpiringSoon(cached.expiresAt)) return@safeCall cached.toDomain()
        }

        val uid = authRemote.getCurrentFirebaseUser()?.uid ?: throw AuthException.UserNotFound()
        val userDto = userRemote.getUser(uid)
        val refreshed = ensureShopifyToken(userDto)
        val accessToken = refreshed.shopifyAccessToken ?: throw AuthException.UnauthorizedAccess()
        val expiresAt = refreshed.shopifyTokenExpiresAt ?: throw AuthException.UnauthorizedAccess()

        ShopifyCustomerToken(refreshed.shopifyCustomerId, accessToken, expiresAt)
    }

    private suspend fun linkOrSignIn(credential: AuthCredential): User {
        val currentUser = authRemote.getCurrentFirebaseUser()

        if (currentUser != null && currentUser.isAnonymous) {
            return try {
                val result = authRemote.linkCurrentUserWithCredential(credential)
                provisionOrRefreshOAuthUser(result, merge = true)
            } catch (_: FirebaseAuthUserCollisionException) {
                val result = authRemote.signInWithCredential(credential)
                val userDoc = userRemote.getUser(result.uid)
                ensureShopifyToken(userDoc).toDomain()
            }
        }

        val result = authRemote.signInWithCredential(credential)
        val existing = userRemote.getUserOrNull(result.uid)
        return if (existing != null) {
            ensureShopifyToken(existing).toDomain()
        } else {
            provisionOrRefreshOAuthUser(result, merge = true)
        }
    }

    private suspend fun provisionOrRefreshOAuthUser(
        result: CredentialAuthResult,
        merge: Boolean
    ): User {
        val baseDto = UserDto(
            id = result.uid,
            fullName = result.fullName,
            email = result.email
        )

        if (baseDto.email.isBlank()) {
            userRemote.saveUser(result.uid, baseDto, merge = merge)
            return baseDto.toDomain()
        }

        val mockPassword = UUID.randomUUID().toString()
        val shopifyFields = provisionShopifyCustomer(
            email = baseDto.email,
            fullName = baseDto.fullName,
            password = mockPassword
        )
        val userDto = baseDto.applyShopifyFields(shopifyFields)
        userRemote.saveUser(result.uid, userDto, merge = merge)
        return userDto.toDomain()
    }

    private suspend fun provisionShopifyCustomer(
        email: String,
        fullName: String,
        password: String
    ): ShopifyFieldsDto {
        val customer = shopifyRemote.createCustomer(email, fullName, password)
        val token = shopifyRemote.createAccessToken(email, password)
        val fields = ShopifyFieldsDto(
            customerId = customer.id,
            accessToken = token.accessToken,
            expiresAt = token.expiresAt,
            password = password
        )
        shopifyLocal?.saveFields(fields)
        return fields
    }

    private suspend fun ensureShopifyToken(userDto: UserDto): UserDto {
        if (userDto.isGuest || userDto.email.isBlank()) return userDto

        val accessToken = userDto.shopifyAccessToken
        val expiresAt = userDto.shopifyTokenExpiresAt

        val needsProvisioning = accessToken == null || expiresAt == null
        val needsRenewal = !needsProvisioning && isExpiringSoon(expiresAt)
        if (!needsProvisioning && !needsRenewal) return userDto

        val fields = try {
            if (needsProvisioning) {
                val password = userDto.shopifyPassword ?: UUID.randomUUID().toString()
                provisionShopifyCustomer(userDto.email, userDto.fullName, password)
            } else {
                val renewed = shopifyRemote.renewAccessToken(accessToken)
                ShopifyFieldsDto(
                    userDto.shopifyCustomerId,
                    renewed.accessToken,
                    renewed.expiresAt,
                    userDto.shopifyPassword
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            val fallbackPassword = userDto.shopifyPassword ?: throw AuthException.ShopifyTokenUnavailable()
            val token = shopifyRemote.createAccessToken(userDto.email, fallbackPassword)
            ShopifyFieldsDto(
                userDto.shopifyCustomerId,
                token.accessToken,
                token.expiresAt,
                userDto.shopifyPassword
            )
        }

        shopifyLocal?.saveFields(fields)
        userRemote.updateShopifyFields(userDto.id, fields)
        return userDto.applyShopifyFields(fields)
    }

    private fun isExpiringSoon(expiresAt: String): Boolean =
        Instant.now().plus(1, ChronoUnit.HOURS).isAfter(Instant.parse(expiresAt))

    private inline fun <T> safeCall(block: () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }
}