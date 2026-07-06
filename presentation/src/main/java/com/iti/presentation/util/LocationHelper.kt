//
//  LocationHelper.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.presentation.screens.address.AddressContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LocationHelper {

    suspend fun getAddressFromCoordinates(context: Context, lat: Double, lng: Double): Address {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            if (addresses.isNotEmpty()) {
                                continuation.resume(mapAndroidAddressToDomain(addresses[0], lat, lng))
                            } else {
                                continuation.resumeWithException(Exception("No address found"))
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resumeWithException(Exception(errorMessage ?: "Geocoding error"))
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    mapAndroidAddressToDomain(addresses[0], lat, lng)
                } else {
                    throw Exception("No address found")
                }
            }
        }
    }

    private fun mapAndroidAddressToDomain(
        addressObj: android.location.Address,
        lat: Double,
        lng: Double
    ): Address {
        val street = addressObj.thoroughfare ?: addressObj.subThoroughfare ?: addressObj.getAddressLine(0) ?: ""
        val city = addressObj.locality ?: addressObj.subAdminArea ?: ""
        val postalCode = addressObj.postalCode ?: ""
        val country = addressObj.countryName ?: ""
        return Address(
            id = UUID.randomUUID().toString(),
            name = "",
            street = street,
            city = city,
            postalCode = postalCode,
            country = country,
            latitude = lat,
            longitude = lng,
            isDefault = false
        )
    }

    suspend fun searchLocationByName(context: Context, query: String, apiKey: String): LocationCoordinates? {
        val systemResult = searchLocationByNameWithGeocoder(context, query)
        if (systemResult != null) return systemResult

        val suggestions = getSuggestionsFromGooglePlaces(query, apiKey)
        if (suggestions.isNotEmpty()) {
            return LocationCoordinates(suggestions[0].latitude, suggestions[0].longitude)
        }
        return null
    }

    private suspend fun searchLocationByNameWithGeocoder(context: Context, query: String): LocationCoordinates? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
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
                        LocationCoordinates(addressObj.latitude, addressObj.longitude)
                    } else {
                        null
                    }
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun getSuggestions(context: Context, query: String, apiKey: String): List<AddressContract.PlaceSuggestion> {
        // Try Google Places Text Search first (highly optimized for POIs like ITI, Smart Village, and addresses)
        val googleSuggestions = getSuggestionsFromGooglePlaces(query, apiKey)
        if (googleSuggestions.isNotEmpty()) {
            return googleSuggestions
        }
        // Fallback to system Geocoder
        return getSuggestionsFromGeocoder(context, query)
    }

    private suspend fun getSuggestionsFromGeocoder(context: Context, query: String): List<AddressContract.PlaceSuggestion> {
        return withContext(Dispatchers.IO) {
            val suggestions = mutableListOf<AddressContract.PlaceSuggestion>()
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val result = suspendCancellableCoroutine<List<AddressContract.PlaceSuggestion>> { continuation ->
                        geocoder.getFromLocationName(query, 5, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<android.location.Address>) {
                                val list = addresses.map { addr ->
                                    val displayName = addr.getAddressLine(0) ?: "${addr.featureName ?: ""}, ${addr.adminArea ?: ""}"
                                    AddressContract.PlaceSuggestion(displayName, addr.latitude, addr.longitude)
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
                            suggestions.add(AddressContract.PlaceSuggestion(displayName, addr.latitude, addr.longitude))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            suggestions
        }
    }

    suspend fun getSuggestionsFromGooglePlaces(query: String, apiKey: String): List<AddressContract.PlaceSuggestion> {
        return withContext(Dispatchers.IO) {
            val suggestions = mutableListOf<AddressContract.PlaceSuggestion>()
            var connection: HttpURLConnection? = null
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=$encodedQuery&key=$apiKey")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonObject = org.json.JSONObject(response.toString())
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
                            suggestions.add(AddressContract.PlaceSuggestion(displayName, lat, lon))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
            suggestions
        }
    }
}
