package com.iti.data.repositories

import com.iti.domain.models.Result
import com.iti.domain.models.order.*
import com.iti.domain.repositories.orders.OrdersRepository
import kotlinx.coroutines.delay

class OrdersRepositoryImpl : OrdersRepository {

    override suspend fun getOrders(): Result<List<Order>> {
        delay(500)
        return Result.Success(sampleOrders)
    }

    override suspend fun getOrderDetails(orderId: String): Result<OrderDetails> {
        delay(350)
        val details = sampleDetails[orderId]
        return if (details != null) Result.Success(details)
        else Result.Failure(Exception("Order not found"))
    }

    companion object {
        val sampleOrders = listOf(
            Order(
                id = "1001",
                name = "#ORD-2024-1001",
                createdAt = "2024-05-20T10:30:00Z",
                totalPrice = 120.50,
                currencyCode = "USD",
                fulfillmentStatus = OrderStatus.PENDING,
                itemsCount = 3
            ),
            Order(
                id = "1000",
                name = "#ORD-2024-1000",
                createdAt = "2024-05-18T16:15:00Z",
                totalPrice = 75.00,
                currencyCode = "USD",
                fulfillmentStatus = OrderStatus.COMPLETED,
                itemsCount = 2
            ),
            Order(
                id = "0999",
                name = "#ORD-2024-0999",
                createdAt = "2024-05-15T11:20:00Z",
                totalPrice = 45.00,
                currencyCode = "USD",
                fulfillmentStatus = OrderStatus.CANCELLED,
                itemsCount = 1
            )
        )

        val sampleDetails = mapOf(
            "1001" to OrderDetails(
                id = "1001",
                name = "#ORD-2024-1001",
                createdAt = "2024-05-20T10:30:00Z",
                financialStatus = "PENDING",
                fulfillmentStatus = OrderStatus.PENDING,
                subtotalPrice = 110.00,
                totalShippingPrice = 10.50,
                totalPrice = 120.50,
                totalDiscounts = 0.0,
                currencyCode = "USD",
                shippingAddress = ShippingAddress(
                    firstName = "Ahmed", lastName = "Mostafa",
                    address1 = "12 Nile St.", city = "Cairo",
                    country = "Egypt", zip = "11511"
                ),
                lineItems = listOf(
                    OrderLineItem("Classic T-Shirt", 2, "Black / M", 25.00, null),
                    OrderLineItem("Denim Jacket", 1, "Blue / L", 60.00, null)
                )
            ),
            "1000" to OrderDetails(
                id = "1000",
                name = "#ORD-2024-1000",
                createdAt = "2024-05-18T16:15:00Z",
                financialStatus = "PAID",
                fulfillmentStatus = OrderStatus.COMPLETED,
                subtotalPrice = 70.00,
                totalShippingPrice = 5.00,
                totalPrice = 75.00,
                totalDiscounts = 0.0,
                currencyCode = "USD",
                shippingAddress = ShippingAddress(
                    firstName = "Sara", lastName = "Ali",
                    address1 = "5 Tahrir Sq.", city = "Giza",
                    country = "Egypt", zip = "12111"
                ),
                lineItems = listOf(
                    OrderLineItem("Running Shoes", 1, "White / 42", 70.00, null)
                )
            ),
            "0999" to OrderDetails(
                id = "0999",
                name = "#ORD-2024-0999",
                createdAt = "2024-05-15T11:20:00Z",
                financialStatus = "REFUNDED",
                fulfillmentStatus = OrderStatus.CANCELLED,
                subtotalPrice = 45.00,
                totalShippingPrice = 0.0,
                totalPrice = 45.00,
                totalDiscounts = 0.0,
                currencyCode = "USD",
                shippingAddress = null,
                lineItems = listOf(
                    OrderLineItem("Baseball Cap", 1, "Grey / One Size", 45.00, null)
                )
            )
        )
    }
}