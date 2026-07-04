package com.iti.domain.util

import com.iti.domain.models.Result
import com.iti.domain.models.auth.ShopifyCustomerToken


interface ShopifyTokenProvider {
    suspend fun getValidToken(): Result<ShopifyCustomerToken>
}