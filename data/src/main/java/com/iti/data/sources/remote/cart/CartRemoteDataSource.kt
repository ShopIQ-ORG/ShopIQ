package com.iti.data.sources.remote.cart

import com.iti.data.dto.cart.CartDto

interface CartRemoteDataSource {
    suspend fun getCart(cartId: String): CartDto
    suspend fun createCart(): String
    suspend fun addLines(cartId: String, variantId: String, quantity: Int): CartDto
    suspend fun updateLines(cartId: String, lineId: String, quantity: Int): CartDto
    suspend fun removeLines(cartId: String, lineIds: List<String>): CartDto
    suspend fun updateDiscountCodes(cartId: String, codes: List<String>): CartDto
}
