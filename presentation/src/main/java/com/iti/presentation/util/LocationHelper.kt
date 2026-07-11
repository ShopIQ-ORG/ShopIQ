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
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

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
}
