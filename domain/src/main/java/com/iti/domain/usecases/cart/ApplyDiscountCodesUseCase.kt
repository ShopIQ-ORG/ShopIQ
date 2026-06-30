package com.iti.domain.usecases.cart

import com.iti.domain.models.cart.Cart
import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository

class ApplyDiscountCodesUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(codes: List<String>): Result<Cart> =
        repository.applyDiscountCodes(codes)
}
