package com.iti.data.sources.remote

import com.iti.data.dto.ShopifyResponse

interface ProductsRemoteDataSource {
    suspend fun getProductsByNumber(first: Int = 10): ShopifyResponse
}