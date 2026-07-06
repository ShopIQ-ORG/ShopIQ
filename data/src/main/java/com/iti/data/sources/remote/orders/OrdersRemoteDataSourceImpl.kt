package com.iti.data.sources.remote.orders

import com.apollographql.apollo.ApolloClient
import com.iti.data.dto.orders.OrderDto
import com.iti.data.mappers.toDto
import com.iti.data.storefront.GetCustomerOrdersQuery
import com.iti.domain.exceptions.OrderException

class OrdersRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : OrdersRemoteDataSource {

    override suspend fun getOrders(customerAccessToken: String): List<OrderDto> {
        val response = apolloClient.query(GetCustomerOrdersQuery(customerAccessToken)).execute()

        if (response.hasErrors()) {
            throw OrderException.GraphQLError(response.errors.orEmpty().map { it.message })
        }

        val customer = response.data?.customer
            ?: throw OrderException.UnauthorizedAccess()

        return customer.orders.nodes.map { it.toDto() }
    }
}