package com.iti.data.repositories

import com.iti.data.mappers.toDomain
import com.iti.data.sources.remote.orders.OrdersRemoteDataSource
import com.iti.data.utils.handleException
import com.iti.domain.exceptions.OrderException
import com.iti.domain.models.Result
import com.iti.domain.models.order.Order
import com.iti.domain.repositories.orders.OrdersRepository
import com.iti.domain.util.ShopifyTokenProvider
import kotlinx.coroutines.CancellationException
import kotlin.collections.map

class OrdersRepositoryImpl(
    private val remote: OrdersRemoteDataSource,
    private val tokenProvider: ShopifyTokenProvider
) : OrdersRepository {

    override suspend fun getOrders(): Result<List<Order>> = safeCall {
        val tokenResult = tokenProvider.getValidToken()
        val accessToken = (tokenResult as? Result.Success)?.data?.accessToken
            ?: throw OrderException.UnauthorizedAccess()

        remote.getOrders(accessToken).map { it.toDomain() }
    }

    private inline fun <T> safeCall(block: () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(e.handleException())
        }
    }
}