package com.iti.data.sources.remote

import com.iti.data.dto.ShopifyResponse
import com.iti.data.GetMainCategoriesQuery

interface ProductsRemoteDataSource {
    suspend fun getProductsByNumber(first: Int = 10): ShopifyResponse
    suspend fun getProductDetails(productId: Long): ShopifyResponse
    suspend fun getMainCategories(): GetMainCategoriesQuery.Data
}