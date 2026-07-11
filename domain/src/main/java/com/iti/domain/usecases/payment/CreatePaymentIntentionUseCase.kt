package com.iti.domain.usecases.payment

import com.iti.domain.models.Result
import com.iti.domain.models.order.PaymobIntentionResult
import com.iti.domain.repositories.order.OrderRepository

class CreatePaymentIntentionUseCase(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(amountCents: Long, currency: String, integrationId: Int): Result<PaymobIntentionResult> {
        return orderRepository.createPaymentIntention(amountCents, currency, integrationId)
    }
}
