package com.iti.data.sources.remote.user

import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto

interface UserRemoteDataSource {
    suspend fun getUser(uid: String): UserDto
    suspend fun getUserOrNull(uid: String): UserDto?
    suspend fun saveUser(uid: String, user: UserDto, merge: Boolean = false)
    suspend fun updateShopifyFields(uid: String, fields: ShopifyFieldsDto)
}