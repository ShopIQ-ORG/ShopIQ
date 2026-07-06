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
        isDefault = isDefault
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
        isDefault = isDefault
    )
}
