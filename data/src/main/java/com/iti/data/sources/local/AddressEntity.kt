//
//  AddressEntity.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.sources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean,
    val recipientName: String = "",
    val phone: String = ""
)
