package com.iti.domain.usecases.products

import com.iti.domain.models.Ad
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow

class GetAdsUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(): Flow<Result<List<Ad>>> {
        return repository.getAds()
    }
}
