package com.iti.data.sources.remote

import com.apollographql.apollo.ApolloClient
import com.iti.data.GetProductsQuery
import com.iti.data.dto.ShopifyResponse
import com.iti.data.mappers.toShopifyResponse

class ProductsRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : ProductsRemoteDataSource {

    override suspend fun getProductsByNumber(first: Int): ShopifyResponse {
        val response = apolloClient.query(GetProductsQuery(first)).execute()

        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }

        val data = response.data ?: throw Exception("Response data is null")
        return data.toShopifyResponse()
    }
}
