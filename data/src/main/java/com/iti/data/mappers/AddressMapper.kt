//
//  AddressMapper.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.mappers

import com.iti.data.sources.local.AddressEntity
import com.iti.domain.models.Address

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        id = id,
        name = name,
        street = street,
        city = city,
        postalCode = postalCode,
        country = country,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault,
        recipientName = recipientName,
        phone = phone
    )
}

fun AddressEntity.toDomain(): Address {
    return Address(
        id = id,
        name = name,
        street = street,
        city = city,
        postalCode = postalCode,
        country = country,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault,
        recipientName = recipientName,
        phone = phone
    )
}
