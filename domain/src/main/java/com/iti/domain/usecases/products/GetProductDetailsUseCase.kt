package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductDetailsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(productId: Long): Flow<Result<Product>> {
        return repository.getProductDetails(productId)
    }
}
