package com.iti.domain.usecases.orders

import com.iti.domain.models.Result
import com.iti.domain.models.order.OrderDetails
import com.iti.domain.repositories.orders.OrdersRepository

class GetOrderDetailsUseCase(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(orderId: String): Result<OrderDetails> =
        repository.getOrderDetails(orderId)
}