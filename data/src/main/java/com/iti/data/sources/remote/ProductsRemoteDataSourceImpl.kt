package com.iti.data.sources.remote

import com.apollographql.apollo.ApolloClient
import com.iti.data.GetCollectionsQuery
import com.iti.data.GetMainCategoriesQuery
import com.iti.data.GetProductsQuery
import com.iti.data.GetProductDetailsQuery
import com.iti.data.dto.AdDto
import com.iti.data.dto.BrandDto
import com.iti.data.dto.ShopifyResponse
import com.iti.data.mappers.toShopifyResponse

class ProductsRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : ProductsRemoteDataSource {

    override suspend fun getProductsByNumber(first: Int, after: String?): ShopifyResponse {
        val response = apolloClient.query(
            GetProductsQuery(
                first = first,
                after = com.apollographql.apollo.api.Optional.presentIfNotNull(after)
            )
        ).execute()

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

    override suspend fun getBrands(): List<BrandDto> {
        val response = apolloClient.query(GetCollectionsQuery(100)).execute()
        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }
        val collections = response.data?.collections?.edges?.map { it.node } ?: emptyList()
        return collections.map { node ->
            BrandDto(
                id = node.id,
                name = node.title,
                imageUrl = node.image?.url?.toString() ?: ""
            )
        }
    }

    override suspend fun getAds(): List<AdDto> {
        val response = apolloClient.query(GetCollectionsQuery(5)).execute()
        if (response.hasErrors()) {
            throw Exception(
                response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error"
            )
        }
        val collections = response.data?.collections?.edges?.map { it.node } ?: emptyList()
        return collections.map { node ->
            AdDto(
                id = node.id,
                imageUrl = node.image?.url?.toString() ?: "",
                title = "NEW COLLECTION",
                subtitle = node.title.uppercase()
            )
        }
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
