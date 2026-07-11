package com.iti.domain.usecases.orders

import com.iti.domain.models.Result
import com.iti.domain.models.order.Order
import com.iti.domain.repositories.order.OrderRepository

class GetOrdersUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(): Result<List<Order>> =
        repository.getOrders()
}