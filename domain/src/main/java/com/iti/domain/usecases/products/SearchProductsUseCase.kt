package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow

class SearchProductsUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Product>>> {
        return repository.searchProducts(query)
    }
}
