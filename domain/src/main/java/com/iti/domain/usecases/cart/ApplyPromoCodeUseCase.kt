package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository


class ApplyPromoCodeUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(code: String): Result<Cart> =
        repository.applyPromoCode(code)
}