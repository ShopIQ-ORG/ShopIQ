package com.iti.domain.usecases.cart
import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository


class AddCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(variantId: String, quantity: Int = 1): Result<Unit> =
        repository.addItem(variantId, quantity)
}