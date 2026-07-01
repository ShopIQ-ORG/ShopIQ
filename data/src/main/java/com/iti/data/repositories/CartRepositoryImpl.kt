package com.iti.data.repositories

import com.iti.data.core.handleException
import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.cart.CartIdDataSource
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.domain.exceptions.CartException
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val cartIdDataSource: CartIdDataSource
) : CartRepository {

    override fun getCart(): Flow<Result<Cart>> = flow {
        emit(Result.Loading)

        try {
            emit(Result.Success(fetchCart()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: CartException.CartNotFound) {
            emit(Result.Success(recreateCart()))
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    private suspend fun fetchCart(): Cart {
        return cartRemoteDataSource
            .getCart(getOrCreateCartId())
            .toDomain()
    }

    private suspend fun recreateCart(): Cart {
        val cartId = cartRemoteDataSource.createCart()

        cartIdDataSource.saveCartId(cartId)

        return cartRemoteDataSource
            .getCart(cartId)
            .toDomain()
    }

    override suspend fun addItem(
        variantId: String,
        quantity: Int
    ): Result<Unit> {
        return try {
            val cartId = getOrCreateCartId()

            cartRemoteDataSource.addLines(
                cartId = cartId,
                variantId = variantId,
                quantity = quantity
            )

            Result.Success(Unit)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun updateItemQuantity(
        lineId: String,
        newQuantity: Int
    ): Result<Unit> {
        return try {
            val cartId = getOrCreateCartId()

            cartRemoteDataSource.updateLines(
                cartId = cartId,
                lineId = lineId,
                quantity = newQuantity
            )

            Result.Success(Unit)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun removeItem(
        lineId: String
    ): Result<Unit> {
        return try {
            val cartId = getOrCreateCartId()

            cartRemoteDataSource.removeLines(
                cartId = cartId,
                lineIds = listOf(lineId)
            )

            Result.Success(Unit)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun applyDiscountCodes(
        codes: List<String>
    ): Result<Cart> {
        return try {
            val cartId = getOrCreateCartId()

            cartRemoteDataSource.updateDiscountCodes(
                cartId = cartId,
                codes = codes
            )

            val updatedCart = cartRemoteDataSource
                .getCart(cartId)
                .toDomain()

            Result.Success(updatedCart)

        } catch (e: CancellationException) {
            throw e

        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartIdDataSource.clearCartId()

            Result.Success(Unit)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    private suspend fun getOrCreateCartId(): String {
        cartIdDataSource.getCartId()?.let {
            return it
        }

        val newCartId = cartRemoteDataSource.createCart()

        cartIdDataSource.saveCartId(newCartId)

        return newCartId
    }
}
