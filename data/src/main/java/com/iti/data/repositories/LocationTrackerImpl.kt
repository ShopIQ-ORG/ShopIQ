package com.iti.data.repositories

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.repositories.location.LocationTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationTrackerImpl(
    private val locationClient: FusedLocationProviderClient,
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationCoordinates? {
        val lastLoc = suspendCancellableCoroutine { continuation ->
            locationClient.lastLocation
                .addOnSuccessListener { loc ->
                    continuation.resume(loc)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    continuation.resume(null)
                }
        }

        if (lastLoc != null) {
            return LocationCoordinates(lastLoc.latitude, lastLoc.longitude)
        }

        return suspendCancellableCoroutine { continuation ->
            val cts = CancellationTokenSource()
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(LocationCoordinates(location.latitude, location.longitude))
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    continuation.cancel()
                }

            continuation.invokeOnCancellation {
                cts.cancel()
            }
        }
    }
}