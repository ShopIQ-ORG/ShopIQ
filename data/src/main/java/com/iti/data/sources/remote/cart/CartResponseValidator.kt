package com.iti.data.sources.remote.cart

import com.apollographql.apollo.api.Error
import com.iti.data.dto.cart.CartDto
import com.iti.domain.exceptions.CartException

class CartResponseValidator {

    fun validateGraphQLErrors(errors: List<Error>?) {
        if (!errors.isNullOrEmpty()) {
            throw CartException.GraphQLError(
                errors.map { it.message }
            )
        }
    }

    fun validateUserErrors(errors: List<String>) {
        if (errors.isNotEmpty()) {
            throw CartException.UserErrors(errors)
        }
    }

    fun validateQuantityUpdated(
        cart: CartDto,
        lineId: String,
        expectedQuantity: Int
    ): CartDto {
        val line = cart.lines.firstOrNull { it.id == lineId }
            ?: throw CartException.OperationRejected(
                operation = "CartLinesUpdate",
                reason = "Updated line was not returned."
            )

        if (line.quantity != expectedQuantity) {
            throw CartException.OperationRejected(
                operation = "CartLinesUpdate",
                reason = "Expected quantity $expectedQuantity but Shopify returned ${line.quantity}."
            )
        }

        return cart
    }

    fun validateLineAdded(
        cart: CartDto,
        variantId: String
    ): CartDto {
        val exists = cart.lines.any { it.variantId == variantId }

        if (!exists) {
            throw CartException.OperationRejected(
                operation = "CartLinesAdd",
                reason = "Added line was not returned by Shopify."
            )
        }

        return cart
    }

    fun validateLinesRemoved(
        cart: CartDto,
        removedIds: List<String>
    ): CartDto {
        val stillExists = cart.lines.any { it.id in removedIds }

        if (stillExists) {
            throw CartException.OperationRejected(
                operation = "CartLinesRemove",
                reason = "One or more lines still exist after removal."
            )
        }

        return cart
    }

    fun validateDiscountCodes(
        cart: CartDto,
        requestedCodes: List<String>
    ): CartDto {
        val applicableCodes = cart.discountCodes
            .filter { it.applicable }
            .map { it.code }

        val missing = requestedCodes.filterNot { it in applicableCodes }

        if (missing.isNotEmpty()) {
            throw CartException.InvalidDiscountCode(
                missing.first()
            )
        }

        return cart
    }
}