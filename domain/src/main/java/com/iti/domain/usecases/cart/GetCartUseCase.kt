package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.models.Result.Failure
import com.iti.domain.models.Result.Loading
import com.iti.domain.models.Result.Success
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GetCartUseCase(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<Cart>> = flow {
        when (val authResult = userRepository.validateAuthenticatedUser()) {
            is Loading -> Loading
            is Failure -> emit(authResult)
            is Success -> emitAll(cartRepository.getCart())
        }
    }
}