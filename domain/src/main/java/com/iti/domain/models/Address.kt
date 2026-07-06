//
//  Address.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.models

data class Address(
    val id: String,
    val name: String,
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val recipientName: String = "",
    val phone: String = ""
)
