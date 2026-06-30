package com.iti.domain.usecases.cart
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import kotlinx.coroutines.flow.Flow

class GetCartUseCase(private val repository: CartRepository) {
    operator fun invoke(): Flow<Result<Cart>> = repository.getCart()
}





