package com.iti.domain.usecases.location
//
//  GetCurrentLocationUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.repositories.location.LocationTracker

class GetCurrentLocationUseCase(private val locationTracker: LocationTracker) {
    suspend operator fun invoke(): LocationCoordinates? {
        return locationTracker.getCurrentLocation()
    }
}