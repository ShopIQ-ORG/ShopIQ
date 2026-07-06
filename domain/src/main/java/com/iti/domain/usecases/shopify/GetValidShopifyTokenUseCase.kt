package com.iti.domain.usecases.shopify

import com.iti.domain.models.Result
import com.iti.domain.models.auth.ShopifyCustomerToken
import com.iti.domain.util.ShopifyTokenProvider

class GetValidShopifyTokenUseCase(
    private val shopifyTokenProvider: ShopifyTokenProvider
) {
    suspend operator fun invoke(): Result<ShopifyCustomerToken> = shopifyTokenProvider.getValidToken()
}