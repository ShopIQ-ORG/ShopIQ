package com.iti.data.sources.remote

import com.iti.data.dto.AdDto
import com.iti.data.dto.BrandDto
import com.iti.data.dto.ShopifyResponse
import com.iti.data.GetMainCategoriesQuery

interface ProductsRemoteDataSource {
    suspend fun getProductsByNumber(first: Int = 10, after: String? = null): ShopifyResponse
    suspend fun getProductDetails(productId: Long): ShopifyResponse
    suspend fun getMainCategories(): GetMainCategoriesQuery.Data
    suspend fun getBrands(): List<BrandDto>
    suspend fun getAds(): List<AdDto>
}
