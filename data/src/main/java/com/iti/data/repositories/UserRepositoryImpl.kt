package com.iti.data.repositories

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.iti.data.dto.auth.CredentialAuthResult
import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.data.mappers.applyShopifyFields
import com.iti.data.mappers.toDomain
import com.iti.data.mappers.toEntity
import com.iti.data.mappers.toUserDto
import com.iti.data.sources.local.AddressDao
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSource
import com.iti.data.sources.remote.user.UserRemoteDataSource
import com.iti.data.utils.handleException
import com.iti.domain.exceptions.AuthException
import com.iti.domain.exceptions.NetworkException
import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.PlaceSuggestion
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.AuthProvider
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.models.auth.ShopifyCustomerToken
import com.iti.domain.repositories.user.UserRepository
import com.iti.domain.util.ShopifyTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.net.URLEncoder
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val authRemote: AuthRemoteDataSource,
    private val userRemote: UserRemoteDataSource,
    private val shopifyRemote: ShopifyCustomerRemoteDataSource,
    private val shopifyLocal: ShopifyTokenLocalDataSource? = null,
    private val addressDao: AddressDao,
    private val client: HttpClient,
    private val context: Context
) : UserRepository, ShopifyTokenProvider {

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
            isEmailVerified = true
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

    override suspend fun updateProfile(
        fullName: String,
        phone: String,
        dateOfBirth: String?,
        gender: String?,
        avatarUrl: String?
    ): Result<User> = safeCall {
        val firebaseUser = authRemote.getCurrentFirebaseUser() ?: throw AuthException.UserNotFound()
        val uid = firebaseUser.uid

        // Update in Firestore
        val existingUserDto = userRemote.getUser(uid)
        val updatedDto = existingUserDto.copy(
            fullName = fullName,
            phone = phone,
            dateOfBirth = dateOfBirth,
            gender = gender,
            avatarUrl = avatarUrl
        )
        userRemote.saveUser(uid, updatedDto, merge = true)

        // Optionally, update Shopify customer if needed, but for now we just update Firestore

        updatedDto.toDomain(
            provider = AuthProvider.fromProviderIds(firebaseUser.providerIds),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }

    override fun getUserId(): String? = authRemote.getCurrentFirebaseUser()?.uid

    override fun isGuest(): Boolean {
        return authRemote.getCurrentFirebaseUser()?.isAnonymous ?: true
    }

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
            email = result.email,
            avatarUrl = result.photoUrl
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
        } catch (e: Exception) {
            val mapped = e.handleException()
            if (mapped is NetworkException) throw mapped

            val fallbackPassword = userDto.shopifyPassword ?: throw AuthException.ShopifyTokenUnavailable()
            val token = try {
                shopifyRemote.createAccessToken(userDto.email, fallbackPassword)
            } catch (fallbackError: CancellationException) {
                throw fallbackError
            } catch (fallbackError: Exception) {
                val fallbackMapped = fallbackError.handleException()
                throw fallbackMapped
            }
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

override fun getSavedAddresses(): Flow<Result<List<Address>>> {
        return addressDao.getAllAddresses()
            .map { entities ->
                Result.Success(entities.map { it.toDomain() }) as Result<List<Address>>
            }
            .catch { e ->
                emit(Result.Failure(Exception(e)))
            }
    }

    override suspend fun saveAddress(address: Address): Result<Unit> {
        return try {
            val count = addressDao.getAddressCount()
            val existing = addressDao.getAddressById(address.id)
            
            val shouldBeDefault = address.isDefault || count == 0 || (existing?.isDefault == true)
            val updatedAddress = address.copy(isDefault = shouldBeDefault)

            if (updatedAddress.isDefault) {
                addressDao.clearDefaultsExcept(updatedAddress.id)
            }

            addressDao.insertAddress(updatedAddress.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val existing = addressDao.getAddressById(addressId)
            if (existing != null) {
                addressDao.deleteAddressById(addressId)
                if (existing.isDefault) {
                    val firstRemaining = addressDao.getFirstAddress()
                    if (firstRemaining != null) {
                        addressDao.insertAddress(firstRemaining.copy(isDefault = true))
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getPlaceSuggestions(query: String, apiKey: String): Result<List<PlaceSuggestion>> {
        return withContext(Dispatchers.IO) {
            try {
                val googleSuggestions = getSuggestionsFromGooglePlaces(query, apiKey)
                if (googleSuggestions.isNotEmpty()) {
                    return@withContext Result.Success(googleSuggestions)
                }
                val geocoderSuggestions = getSuggestionsFromGeocoder(query)
                Result.Success(geocoderSuggestions)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    override suspend fun searchLocationByName(query: String, apiKey: String): Result<LocationCoordinates?> {
        return withContext(Dispatchers.IO) {
            try {
                val systemResult = searchLocationByNameWithGeocoder(query)
                if (systemResult != null) {
                    return@withContext Result.Success(systemResult)
                }

                val suggestions = getSuggestionsFromGooglePlaces(query, apiKey)
                if (suggestions.isNotEmpty()) {
                    return@withContext Result.Success(LocationCoordinates(suggestions[0].latitude, suggestions[0].longitude))
                }
                Result.Success(null)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }

    private suspend fun getSuggestionsFromGooglePlaces(query: String, apiKey: String): List<PlaceSuggestion> {
        val suggestions = mutableListOf<PlaceSuggestion>()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$encodedQuery&key=$apiKey"
            val responseBody = client.get(url).bodyAsText()
            val jsonObject = org.json.JSONObject(responseBody)
            val status = jsonObject.optString("status", "")
            if (status == "OK" || status == "ZERO_RESULTS") {
                val jsonArray = jsonObject.getJSONArray("results")
                for (i in 0 until minOf(jsonArray.length(), 5)) {
                    val result = jsonArray.getJSONObject(i)
                    val name = result.getString("name")
                    val formattedAddress = result.getString("formatted_address")
                    val displayName = "$name, $formattedAddress"
                    val geometry = result.getJSONObject("geometry")
                    val location = geometry.getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lon = location.getDouble("lng")
                    suggestions.add(PlaceSuggestion(displayName, lat, lon))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return suggestions
    }

    private suspend fun getSuggestionsFromGeocoder(query: String): List<PlaceSuggestion> {
        val suggestions = mutableListOf<PlaceSuggestion>()
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val result = suspendCancellableCoroutine<List<PlaceSuggestion>> { continuation ->
                    geocoder.getFromLocationName(query, 5, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            val list = addresses.map { addr ->
                                val displayName = addr.getAddressLine(0) ?: "${addr.featureName ?: ""}, ${addr.adminArea ?: ""}"
                                PlaceSuggestion(displayName, addr.latitude, addr.longitude)
                            }
                            continuation.resume(list)
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(emptyList())
                        }
                    })
                }
                suggestions.addAll(result)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 5)
                if (!addresses.isNullOrEmpty()) {
                    addresses.forEach { addr ->
                        val displayName = addr.getAddressLine(0) ?: "${addr.featureName ?: ""}, ${addr.adminArea ?: ""}"
                        suggestions.add(PlaceSuggestion(displayName, addr.latitude, addr.longitude))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return suggestions
    }

    private suspend fun searchLocationByNameWithGeocoder(query: String): LocationCoordinates? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(query, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            if (addresses.isNotEmpty()) {
                                val addressObj = addresses[0]
                                continuation.resume(LocationCoordinates(addressObj.latitude, addressObj.longitude))
                            } else {
                                continuation.resume(null)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resume(null)
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addressObj = addresses[0]
                    return LocationCoordinates(addressObj.latitude, addressObj.longitude)
                }
            }
        } catch (_: Exception) {}
        return null
    }
}
