package com.iti.domain.repositories.order

import com.iti.domain.models.Result
import com.iti.domain.models.order.Order
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.order.PaymobIntentionResult

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
    
    suspend fun createDraftOrder(cart: Cart, shippingAddress: Address?, email: String?): Result<DraftOrder>
    suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder>
    
    suspend fun createPaymentIntention(amountCents: Long, currency: String, integrationId: Int): Result<PaymobIntentionResult>
}
