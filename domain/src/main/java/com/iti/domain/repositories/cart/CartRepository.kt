package com.iti.domain.repositories.cart
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<Result<Cart>>
    suspend fun increaseQuantity(itemId: String): Result<Unit>
    suspend fun decreaseQuantity(itemId: String): Result<Unit>
    suspend fun removeItem(itemId: String): Result<Unit>
    suspend fun applyPromoCode(code: String): Result<Cart>
    suspend fun clearCart(): Result<Unit>
}