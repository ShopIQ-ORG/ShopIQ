package com.iti.domain.repositories.location

import com.iti.domain.models.LocationCoordinates
//
//  LocationTracker.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//
interface LocationTracker {
    suspend fun getCurrentLocation(): LocationCoordinates?
}