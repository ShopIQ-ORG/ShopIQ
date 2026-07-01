package com.iti.data.sources.remote.cart

import com.apollographql.apollo.ApolloClient
import com.iti.data.core.executeOrThrow
import com.iti.data.dto.cart.CartDto
import com.iti.data.mappers.toDto
import com.iti.data.storefront.CartCreateMutation
import com.iti.data.storefront.CartDiscountCodesUpdateMutation
import com.iti.data.storefront.CartLinesAddMutation
import com.iti.data.storefront.CartLinesRemoveMutation
import com.iti.data.storefront.CartLinesUpdateMutation
import com.iti.data.storefront.GetCartQuery
import com.iti.data.storefront.type.CartLineInput
import com.iti.data.storefront.type.CartLineUpdateInput
import com.iti.domain.exceptions.CartException

class CartRemoteDataSourceImpl(
    private val apolloClient: ApolloClient,
    private val validator: CartResponseValidator
) : CartRemoteDataSource {

    override suspend fun getCart(cartId: String): CartDto {
        val response = apolloClient.query(
            GetCartQuery(cartId)
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        val cart = response.data?.cart
            ?: throw CartException.CartNotFound()

        return cart.cartFields.toDto()
    }

    override suspend fun createCart(): String {
        val response = apolloClient.mutation(
            CartCreateMutation()
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        validator.validateUserErrors(
            response.data?.cartCreate?.userErrors
                ?.map { it.message }
                .orEmpty()
        )

        return response.data?.cartCreate?.cart?.cartFields?.id
            ?: throw CartException.CartNotFound()
    }

    override suspend fun addLines(
        cartId: String,
        variantId: String,
        quantity: Int
    ): CartDto {
        if (quantity <= 0) {
            throw CartException.InvalidQuantity()
        }

        val response = apolloClient.mutation(
            CartLinesAddMutation(
                cartId = cartId,
                lines = listOf(
                    CartLineInput(merchandiseId = variantId, quantity = quantity)
                )
            )
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        validator.validateUserErrors(
            response.data?.cartLinesAdd?.userErrors
                ?.map { it.message }
                .orEmpty()
        )

        val cart = response.data?.cartLinesAdd?.cart
            ?: throw CartException.CartNotFound()

        val dto = cart.cartFields.toDto()

        return validator.validateLineAdded(cart = dto, variantId = variantId)
    }

    override suspend fun updateLines(
        cartId: String,
        lineId: String,
        quantity: Int
    ): CartDto {
        if (quantity < 0) {
            throw CartException.InvalidQuantity()
        }

        val response = apolloClient.mutation(
            CartLinesUpdateMutation(
                cartId = cartId,
                lines = listOf(
                    CartLineUpdateInput(id = lineId, quantity = quantity)
                )
            )
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        validator.validateUserErrors(
            response.data?.cartLinesUpdate?.userErrors
                ?.map { it.message }
                .orEmpty()
        )

        val cart = response.data?.cartLinesUpdate?.cart
            ?: throw CartException.CartNotFound()

        val dto = cart.cartFields.toDto()

        return validator.validateQuantityUpdated(
            cart = dto,
            lineId = lineId,
            expectedQuantity = quantity
        )
    }

    override suspend fun removeLines(cartId: String, lineIds: List<String>) : CartDto{
        val response = apolloClient.mutation(
            CartLinesRemoveMutation(
                cartId = cartId,
                lineIds = lineIds
            )
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        validator.validateUserErrors(
            response.data?.cartLinesRemove?.userErrors
                ?.map { it.message }
                .orEmpty()
        )

        val cart = getCart(cartId)

        return validator.validateLinesRemoved(cart = cart, removedIds = lineIds)
    }

    override suspend fun updateDiscountCodes(cartId: String, codes: List<String>): CartDto {
        val response = apolloClient.mutation(
            CartDiscountCodesUpdateMutation(
                cartId = cartId,
                discountCodes = codes
            )
        ).executeOrThrow()

        validator.validateGraphQLErrors(response.errors)

        validator.validateUserErrors(
            response.data?.cartDiscountCodesUpdate?.userErrors
                ?.map { it.message }
                .orEmpty()
        )

        val cart = response.data?.cartDiscountCodesUpdate?.cart
            ?: throw CartException.CartNotFound()

        val dto = cart.cartFields.toDto()

        return validator.validateDiscountCodes(cart = dto, requestedCodes = codes)
    }
}
