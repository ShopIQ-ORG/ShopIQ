package com.iti.domain.repositories.cart

import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.util.CacheInvalidator
import kotlinx.coroutines.flow.Flow

interface CartRepository : CacheInvalidator {
    fun getCart(): Flow<Result<Cart>>
    suspend fun addItem(variantId: String, quantity: Int = 1): Result<Unit>
    suspend fun updateItemQuantity(lineId: String, newQuantity: Int): Result<Unit>
    suspend fun removeItem(lineId: String): Result<Unit>
    suspend fun applyDiscountCodes(codes: List<String>): Result<Cart>
    suspend fun clearCart(): Result<Unit>
}