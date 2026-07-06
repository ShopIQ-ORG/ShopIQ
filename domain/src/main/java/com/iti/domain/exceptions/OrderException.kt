package com.iti.domain.exceptions

sealed class OrderException(message: String) : AppException(message) {

    class GraphQLError(
        val errors: List<String>
    ) : OrderException(
        "GraphQL error: ${errors.joinToString("; ")}"
    )

    class OrderNotFound(
        val orderId: String
    ) : OrderException(
        "Order '$orderId' was not found."
    )

    class NoOrdersFound : OrderException(
        "No orders found for this account."
    )

    class UnauthorizedAccess : OrderException(
        "You must be signed in to view your orders."
    )
}