package com.iti.data.sources.local.shopify

import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto


interface ShopifyTokenLocalDataSource {
    suspend fun getCachedFields(): ShopifyFieldsDto?
    suspend fun saveFields(fields: ShopifyFieldsDto)
    suspend fun clear()
}