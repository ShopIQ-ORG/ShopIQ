package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.models.Result.Failure
import com.iti.domain.models.Result.Loading
import com.iti.domain.models.Result.Success
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.user.UserRepository

class ApplyDiscountCodesUseCase(
    private val repository: CartRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(codes: List<String>): Result<Cart> {
        return when (val authResult = userRepository.validateAuthenticatedUser()) {
            is Failure -> authResult
            is Success -> repository.applyDiscountCodes(codes)
            Loading -> Loading
        }
    }
}