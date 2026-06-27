package com.iti.data.mappers
import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.User

fun UserDto.toDomain(): User {
    return User(
        uid = this.id,
        fullName = this.fullName,
        email = this.email,
        phone = this.phone
    )
}
