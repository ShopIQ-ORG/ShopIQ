package com.iti.data.sources.remote

import com.iti.data.dto.AdDto
import com.iti.data.dto.BrandDto
import com.iti.data.dto.ShopifyResponse
import com.iti.data.GetAllCategoriesQuery
import com.iti.data.GetProductsInCollectionQuery

interface ProductsRemoteDataSource {
    suspend fun getProductsByNumber(first: Int = 10, after: String? = null): ShopifyResponse
    suspend fun getProducts(
        first: Int = 10,
        query: String? = null,
        sortKey: com.iti.data.type.ProductSortKeys? = null,
        reverse: Boolean? = null
    ): ShopifyResponse
    suspend fun getProductDetails(productId: Long): ShopifyResponse
    suspend fun getMainCategories(): GetAllCategoriesQuery.Data
    suspend fun getProductsByCategory(categoryId: String, first: Int = 10): GetProductsInCollectionQuery.Data
    suspend fun getBrands(): List<BrandDto>
    suspend fun getAds(): List<AdDto>
    suspend fun createProductReview(
        productId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String,
        avatarUrl: String
    ): String
    suspend fun setProductReviews(
        productId: String,
        reviewIds: List<String>
    )
    suspend fun updateProductReview(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        createdAt: String,
        avatarUrl: String
    )
    suspend fun deleteProductReview(reviewId: String)
    suspend fun getProductTranslations(productId: String): Map<String, String>
    suspend fun getCollectionTranslations(collectionId: String, locale: String): Map<String, String>?
    suspend fun saveCollectionTranslation(collectionId: String, locale: String, title: String, bodyHtml: String)
}
