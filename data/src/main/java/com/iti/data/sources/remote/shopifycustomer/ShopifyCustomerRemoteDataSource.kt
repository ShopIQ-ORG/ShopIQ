package com.iti.data.sources.remote.shopifycustomer
import com.iti.data.dto.shopifycustomer.ShopifyCustomerDto
import com.iti.data.dto.shopifycustomer.ShopifyCustomerTokenDto

interface ShopifyCustomerRemoteDataSource {
    suspend fun createCustomer(email: String, fullName: String, password: String): ShopifyCustomerDto
    suspend fun createAccessToken(email: String, password: String): ShopifyCustomerTokenDto
    suspend fun renewAccessToken(accessToken: String): ShopifyCustomerTokenDto
}