package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.models.Result.Failure
import com.iti.domain.models.Result.Loading
import com.iti.domain.models.Result.Success
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.cart.CartRepository

class UpdateCartItemQuantityUseCase(
    private val repository: CartRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        lineId: String,
        newQuantity: Int
    ): Result<Unit> {
        return when (val authResult = authRepository.validateAuthenticatedUser()) {
            is Failure -> authResult
            is Success -> repository.updateItemQuantity(lineId, newQuantity)
            Loading -> Loading
        }
    }
}