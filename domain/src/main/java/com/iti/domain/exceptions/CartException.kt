package com.iti.domain.exceptions

sealed class CartException(message: String) : AppException(message) {

    class UserErrors(
        val errors: List<String>
    ) : CartException(
        "Cart operation failed: ${errors.joinToString("; ")}"
    )

    class GraphQLError(
        val errors: List<String>
    ) : CartException(
        "GraphQL error: ${errors.joinToString("; ")}"
    )

    class OperationRejected(
        val operation: String,
        val reason: String
    ) : CartException(
        "$operation failed. $reason"
    )

    class CartNotFound : CartException(
        "Cart not found. It may have expired."
    )

    class InvalidQuantity : CartException(
        "Quantity must be greater than zero."
    )

    class InvalidDiscountCode(
        code: String
    ) : CartException(
        "Discount code '$code' is not applicable."
    )
}