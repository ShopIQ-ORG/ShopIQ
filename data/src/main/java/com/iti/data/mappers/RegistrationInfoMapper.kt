package com.iti.data.mappers
import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.auth.RegistrationInfo

fun RegistrationInfo.toUserDto(uid: String) = UserDto(
    id = uid,
    fullName = fullName,
    email = email,
    phone = phone
)