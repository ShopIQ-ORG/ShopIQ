package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository

class UpdateCartItemQuantityUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(lineId: String, newQuantity: Int): Result<Unit> =
        repository.updateItemQuantity(lineId, newQuantity)
}
