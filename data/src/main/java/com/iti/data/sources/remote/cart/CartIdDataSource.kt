package com.iti.data.sources.remote.cart

interface CartIdDataSource {
    suspend fun getCartId(): String?
    suspend fun saveCartId(cartId: String)
    suspend fun clearCartId()
}