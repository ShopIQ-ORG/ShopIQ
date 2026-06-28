package com.iti.data.sources.remote

import com.iti.data.dto.AdDto
import com.iti.data.dto.BrandDto
import com.iti.data.dto.ShopifyResponse

interface ProductsRemoteDataSource {
    suspend fun getProductsByNumber(first: Int = 10): ShopifyResponse
    suspend fun getBrands(): List<BrandDto>
    suspend fun getAds(): List<AdDto>
}
