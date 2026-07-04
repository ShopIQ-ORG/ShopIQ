package com.iti.data.repositories
import com.iti.data.utils.handleException
import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.cart.CartIdDataSource
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.domain.exceptions.CartException
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val cartIdDataSource: CartIdDataSource
) : CartRepository {

    private val cartState = MutableStateFlow<Result<Cart>>(Result.Loading)

    override fun getCart(): Flow<Result<Cart>> = cartState
        .onSubscription {
            if (cartState.value !is Result.Success) refreshCart()
        }

    private suspend fun refreshCart() {
        cartState.value = Result.Loading
        cartState.value = try {
            Result.Success(fetchCart())
        } catch (e: CancellationException) {
            throw e
        } catch (_: CartException.CartNotFound) {
            Result.Success(recreateCart())
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    private suspend fun fetchCart(): Cart =
        cartRemoteDataSource.getCart(getOrCreateCartId()).toDomain()

    private suspend fun recreateCart(): Cart {
        val cartId = cartRemoteDataSource.createCart()
        cartIdDataSource.saveCartId(cartId)
        return cartRemoteDataSource.getCart(cartId).toDomain()
    }

    override suspend fun addItem(variantId: String, quantity: Int): Result<Unit> {
        return try {
            val cart = cartRemoteDataSource.addLines(getOrCreateCartId(), variantId, quantity)
            cartState.value = Result.Success(cart.toDomain())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun updateItemQuantity(lineId: String, newQuantity: Int): Result<Unit> {
        return try {
            val cart = cartRemoteDataSource.updateLines(getOrCreateCartId(), lineId, newQuantity)
            cartState.value = Result.Success(cart.toDomain())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun removeItem(lineId: String): Result<Unit> {
        return try {
            val cart = cartRemoteDataSource.removeLines(getOrCreateCartId(), listOf(lineId))
            cartState.value = Result.Success(cart.toDomain())
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun applyDiscountCodes(codes: List<String>): Result<Cart> {
        return try {
            val cartId = getOrCreateCartId()
            cartRemoteDataSource.updateDiscountCodes(cartId, codes)
            val updated = cartRemoteDataSource.getCart(cartId).toDomain()
            cartState.value = Result.Success(updated)
            Result.Success(updated)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartIdDataSource.clearCartId()
            cartState.value = Result.Loading
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    private suspend fun getOrCreateCartId(): String {
        cartIdDataSource.getCartId()?.let { return it }
        val newCartId = cartRemoteDataSource.createCart()
        cartIdDataSource.saveCartId(newCartId)
        return newCartId
    }

    override suspend fun invalidate() {
        cartState.value = Result.Loading
    }
}