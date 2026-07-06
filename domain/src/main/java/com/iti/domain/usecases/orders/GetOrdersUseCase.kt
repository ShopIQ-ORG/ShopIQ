package com.iti.domain.usecases.orders

import com.iti.domain.models.Result
import com.iti.domain.models.order.Order
import com.iti.domain.repositories.orders.OrdersRepository

class GetOrdersUseCase(
    private val repository: OrdersRepository
) {
    suspend operator fun invoke(): Result<List<Order>> =
        repository.getOrders()
}