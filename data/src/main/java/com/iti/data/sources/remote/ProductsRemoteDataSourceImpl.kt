package com.iti.data.sources.remote

import com.apollographql.apollo.ApolloClient
import com.iti.data.GetMainCategoriesQuery
import com.iti.data.GetProductsQuery
import com.iti.data.GetProductDetailsQuery
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

    override suspend fun getProductDetails(productId: Long): ShopifyResponse {
        val globalId = "gid://shopify/Product/$productId"
        val response = apolloClient.query(GetProductDetailsQuery(globalId)).execute()

        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }

        val productData = response.data?.product ?: throw Exception("Product details data is null")
        return productData.toShopifyResponse()
    }

    override suspend fun getMainCategories(): GetMainCategoriesQuery.Data {
        val response = apolloClient.query(GetMainCategoriesQuery()).execute()

        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }

        return response.data ?: throw Exception("Response data is null")
    }
}
