package com.iti.data.sources.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.iti.data.GetAllCategoriesQuery
import com.iti.data.GetCollectionsQuery
import com.iti.data.GetProductDetailsQuery
import com.iti.data.GetProductsInCollectionQuery
import com.iti.data.GetProductsQuery
import com.iti.data.dto.AdDto
import com.iti.data.dto.BrandDto
import com.iti.data.dto.ShopifyResponse
import com.iti.data.mappers.toShopifyResponse
import com.iti.data.type.ProductSortKeys

class ProductsRemoteDataSourceImpl(
    private val apolloClient: ApolloClient
) : ProductsRemoteDataSource {

    override suspend fun getProductsByNumber(first: Int, after: String?): ShopifyResponse {
        val response = apolloClient.query(
            GetProductsQuery(
                first = first,
                after = Optional.presentIfNotNull(after)
            )
        ).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }

        val data = response.data ?: throw Exception("Response data is null")
        return data.toShopifyResponse()
    }

    override suspend fun getProducts(
        first: Int,
        query: String?,
        sortKey: ProductSortKeys?,
        reverse: Boolean?
    ): ShopifyResponse {
        val response = apolloClient.query(
            GetProductsQuery(
                first = first,
                query = Optional.presentIfNotNull(query),
                sortKey = Optional.presentIfNotNull(sortKey),
                reverse = Optional.presentIfNotNull(reverse)
            )
        ).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }

        val data = response.data ?: throw Exception("Response data is null")
        return data.toShopifyResponse()
    }

    override suspend fun getProductDetails(productId: Long): ShopifyResponse {
        val globalId = "gid://shopify/Product/$productId"
        val response = apolloClient.query(GetProductDetailsQuery(globalId)).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }

        val productData = response.data?.product ?: throw Exception("Product details data is null")
        return productData.toShopifyResponse()
    }

    override suspend fun getBrands(): List<BrandDto> {
        val response = apolloClient.query(GetCollectionsQuery(100)).execute()
        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
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
        // Fetch more to ensure we have enough ads after filtering for images
        val response = apolloClient.query(GetCollectionsQuery(15)).execute()
        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }
        val collections = response.data?.collections?.edges?.map { it.node } ?: emptyList()
        return collections
            .filter { it.image != null }
            .take(5)
            .map { node ->
                AdDto(
                    id = node.id,
                    imageUrl = node.image?.url?.toString() ?: "",
                    title = "NEW COLLECTION",
                    subtitle = node.title.uppercase()
                )
            }
    }

    override suspend fun getMainCategories(): GetAllCategoriesQuery.Data {
        val response = apolloClient.query(GetAllCategoriesQuery()).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }

        return response.data ?: throw Exception("Response data is null")
    }

    override suspend fun getProductsByCategory(categoryId: String): GetProductsInCollectionQuery.Data {
        val response = apolloClient.query(GetProductsInCollectionQuery(categoryId)).execute()

        if (response.hasErrors()) {
            throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown GraphQL error")
        }

        return response.data ?: throw Exception("Collection data is null")
    }
}
