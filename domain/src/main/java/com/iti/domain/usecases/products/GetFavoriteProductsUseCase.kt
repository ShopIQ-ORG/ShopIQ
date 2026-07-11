package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> {
        return repository.getFavorites()
    }
}

