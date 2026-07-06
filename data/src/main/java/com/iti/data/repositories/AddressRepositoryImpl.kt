package com.iti.data.repositories

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.iti.data.mappers.toDomain
import com.iti.data.mappers.toEntity
import com.iti.data.sources.local.AddressDao
import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.PlaceSuggestion
import com.iti.domain.models.Result
import com.iti.domain.repositories.address.AddressRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.Locale
import kotlin.coroutines.resume

class AddressRepositoryImpl(
    private val addressDao: AddressDao,
    private val client: HttpClient,
    private val context: Context
) : AddressRepository {

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
