package com.iti.domain.repositories.orders

import com.iti.domain.models.Result
import com.iti.domain.models.order.Order

interface OrdersRepository {
    suspend fun getOrders(): Result<List<Order>>
}