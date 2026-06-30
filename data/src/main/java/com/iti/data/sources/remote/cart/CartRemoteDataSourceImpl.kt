package com.iti.data.sources.remote.cart

import com.apollographql.apollo.ApolloClient
import com.iti.data.storefront.CartCreateMutation
import com.iti.data.storefront.CartDiscountCodesUpdateMutation
import com.iti.data.storefront.CartLinesAddMutation
import com.iti.data.storefront.CartLinesRemoveMutation
import com.iti.data.storefront.CartLinesUpdateMutation
import com.iti.data.storefront.GetCartQuery
import com.iti.data.dto.cart.CartDto
import com.iti.data.mappers.toDto
import com.iti.domain.exceptions.CartException

class CartRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : CartRemoteDataSource {

    override suspend fun getCart(cartId: String): CartDto {
        val response = apolloClient.query(GetCartQuery(cartId)).execute()
        val cart = response.data?.cart ?: throw CartException.CartNotFound()
        return cart.toDto()
    }

    override suspend fun createCart(): String {
        val response = apolloClient.mutation(CartCreateMutation()).execute()
        val userErrors = response.data?.cartCreate?.userErrors ?: emptyList()
        if (userErrors.isNotEmpty()) {
            throw CartException.UserErrors(userErrors.map { it.message })
        }
        return response.data?.cartCreate?.cart?.id ?: error("Failed to create cart")
    }

    override suspend fun addLines(cartId: String, variantId: String, quantity: Int): CartDto {
        val response = apolloClient.mutation(
            CartLinesAddMutation(
                cartId = cartId,
                lines = listOf(
                    com.iti.data.storefront.type.CartLineInput(
                        merchandiseId = variantId,
                        quantity = quantity
                    )
                )
            )
        ).execute()
        val userErrors = response.data?.cartLinesAdd?.userErrors ?: emptyList()
        if (userErrors.isNotEmpty()) {
            throw CartException.UserErrors(userErrors.map { it.message })
        }
        val cart = response.data?.cartLinesAdd?.cart ?: throw CartException.CartNotFound()
        return cart.toDto()
    }

    override suspend fun updateLines(cartId: String, lineId: String, quantity: Int): CartDto {
        val response = apolloClient.mutation(
            CartLinesUpdateMutation(
                cartId = cartId,
                lines = listOf(
                    com.iti.data.storefront.type.CartLineUpdateInput(
                        id = lineId,
                        quantity = quantity
                    )
                )
            )
        ).execute()
        val userErrors = response.data?.cartLinesUpdate?.userErrors ?: emptyList()
        if (userErrors.isNotEmpty()) {
            throw CartException.UserErrors(userErrors.map { it.message })
        }
        val cart = response.data?.cartLinesUpdate?.cart ?: throw CartException.CartNotFound()
        return cart.toDto()
    }

    override suspend fun removeLines(cartId: String, lineIds: List<String>) {
        val response = apolloClient.mutation(
            CartLinesRemoveMutation(
                cartId = cartId,
                lineIds = lineIds
            )
        ).execute()
        val userErrors = response.data?.cartLinesRemove?.userErrors ?: emptyList()
        if (userErrors.isNotEmpty()) {
            throw CartException.UserErrors(userErrors.map { it.message })
        }
    }

    override suspend fun updateDiscountCodes(cartId: String, codes: List<String>): CartDto {
        val response = apolloClient.mutation(
            CartDiscountCodesUpdateMutation(
                cartId = cartId,
                discountCodes = codes
            )
        ).execute()
        val userErrors = response.data?.cartDiscountCodesUpdate?.userErrors ?: emptyList()
        if (userErrors.isNotEmpty()) {
            throw CartException.UserErrors(userErrors.map { it.message })
        }
        val cart = response.data?.cartDiscountCodesUpdate?.cart ?: throw CartException.CartNotFound()
        return cart.toDto()
    }


}
