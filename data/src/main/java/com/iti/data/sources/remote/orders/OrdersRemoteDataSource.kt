package com.iti.data.sources.remote.orders

import com.iti.data.dto.orders.OrderDto

interface OrdersRemoteDataSource {
    suspend fun getOrders(customerAccessToken: String): List<OrderDto>
}