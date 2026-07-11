package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.models.Result.Failure
import com.iti.domain.models.Result.Loading
import com.iti.domain.models.Result.Success
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.user.UserRepository

class UpdateCartItemQuantityUseCase(
    private val repository: CartRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        lineId: String,
        newQuantity: Int
    ): Result<Unit> {
        return when (val authResult = userRepository.validateAuthenticatedUser()) {
            is Failure -> authResult
            is Success -> repository.updateItemQuantity(lineId, newQuantity)
            Loading -> Loading
        }
    }
}