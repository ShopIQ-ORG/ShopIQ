package com.iti.domain.repositories.orders

import com.iti.domain.models.Result
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.OrderDetails

interface OrdersRepository {
    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrderDetails(orderId: String): Result<OrderDetails>
}