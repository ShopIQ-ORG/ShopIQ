package com.iti.domain.usecases.products

import com.iti.domain.models.Ad
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetAdsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Ad>>> {
        return repository.getAds()
    }
}
