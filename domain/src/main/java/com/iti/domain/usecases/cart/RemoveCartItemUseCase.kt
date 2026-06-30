package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository

class RemoveCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(lineId: String): Result<Unit> =
        repository.removeItem(lineId)
}
