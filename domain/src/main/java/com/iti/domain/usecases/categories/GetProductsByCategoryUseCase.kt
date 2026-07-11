package com.iti.domain.usecases.categories

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsByCategoryUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(categoryId: String): Flow<Result<List<Product>>> {
        return repository.getProductsByCategory(categoryId)
    }
}
