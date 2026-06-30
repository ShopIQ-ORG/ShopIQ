package com.iti.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.core.FirebaseConstants
import com.iti.data.core.handleException
import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.domain.exceptions.CartException
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.cart.CartRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CartRepository {

    override fun getCart(): Flow<Result<Cart>> = flow {
        emit(Result.Loading)
        try {
            val cartId = getOrCreateCartId()
            emit(Result.Success(remoteDataSource.getCart(cartId).toDomain()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: CartException.CartNotFound) {
            val freshId = remoteDataSource.createCart()
            saveCartId(freshId)
            emit(Result.Success(remoteDataSource.getCart(freshId).toDomain()))
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    override suspend fun addItem(variantId: String, quantity: Int): Result<Unit> {
        return try {
            remoteDataSource.addLines(getOrCreateCartId(), variantId, quantity)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun updateItemQuantity(lineId: String, newQuantity: Int): Result<Unit> {
        return try {
            remoteDataSource.updateLines(getOrCreateCartId(), lineId, newQuantity)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun removeItem(lineId: String): Result<Unit> {
        return try {
            remoteDataSource.removeLines(getOrCreateCartId(), listOf(lineId))
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun applyDiscountCodes(codes: List<String>): Result<Cart> {
        return try {
            Result.Success(remoteDataSource.updateDiscountCodes(getOrCreateCartId(), codes).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    override suspend fun clearCart(): Result<Unit> {
        return try {
            userDocument().update(FirebaseConstants.UserFields.CART_ID, null).await()
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }

    private suspend fun getOrCreateCartId(): String {
        val snapshot = userDocument().get().await()
        val existing = snapshot.getString(FirebaseConstants.UserFields.CART_ID)
        if (!existing.isNullOrBlank()) return existing
        val newId = remoteDataSource.createCart()
        saveCartId(newId)
        return newId
    }

    private suspend fun saveCartId(cartId: String) {
        userDocument().update(FirebaseConstants.UserFields.CART_ID, cartId).await()
    }

    private fun userDocument() = firestore
        .collection(FirebaseConstants.Collections.USERS)
        .document(auth.currentUser?.uid ?: error("No authenticated user"))
}
