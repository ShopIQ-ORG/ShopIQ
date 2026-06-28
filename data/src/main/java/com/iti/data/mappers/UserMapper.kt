package com.iti.data.mappers
import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.User

fun UserDto.toDomain(): User {
    return if (isGuest) {
        User.GuestUser
    } else {
        User.AuthenticatedUser(
            uid = id,
            fullName = fullName,
            email = email,
            phone = phone
        )
    }
}