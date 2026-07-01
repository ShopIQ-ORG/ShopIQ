package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveCartItemCountUseCase(
    private val getCartUseCase: GetCartUseCase
) {
    operator fun invoke(): Flow<Int> = getCartUseCase().map { result ->
        when (result) {
            is Result.Success -> result.data.items.size
            else -> 0
        }
    }
}