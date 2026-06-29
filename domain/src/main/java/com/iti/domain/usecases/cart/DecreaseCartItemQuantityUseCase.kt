package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository

class DecreaseCartItemQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(itemId: String): Result<Unit> =
        repository.decreaseQuantity(itemId)
}