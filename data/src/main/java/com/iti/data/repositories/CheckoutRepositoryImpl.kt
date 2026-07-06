//
//  CheckoutRepositoryImpl.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.repositories

import com.iti.data.dto.checkout.DraftOrderDto
import com.iti.data.sources.remote.checkout.CheckoutRemoteDataSource
import com.iti.data.utils.handleException
import com.iti.domain.models.Result
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.Address
import com.iti.domain.repositories.checkout.CheckoutRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckoutRepositoryImpl(
    private val remoteDataSource: CheckoutRemoteDataSource
) : CheckoutRepository {

    override suspend fun createDraftOrder(
        lineItems: List<Pair<String, Int>>,
        shippingAddress: Address?
    ): Result<DraftOrder> = withContext(Dispatchers.IO) {
        safeCall {
            val street = shippingAddress?.street ?: ""
            val city = shippingAddress?.city ?: ""
            val country = shippingAddress?.country ?: ""
            val zip = shippingAddress?.postalCode ?: ""

            val dto = remoteDataSource.createDraftOrder(
                lineItems = lineItems,
                street = street,
                city = city,
                country = country,
                zip = zip
            )
            dto.toDomain()
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder> = withContext(Dispatchers.IO) {
        safeCall {
            remoteDataSource.completeDraftOrder(draftOrderId).toDomain()
        }
    }

    private fun DraftOrderDto.toDomain(): DraftOrder {
        return DraftOrder(
            id = id,
            totalPrice = totalPrice ?: "0.00",
            subtotalPrice = subtotalPrice ?: "0.00",
            totalTax = totalTax ?: "0.00",
            status = status,
            orderNumber = order?.name
        )
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
