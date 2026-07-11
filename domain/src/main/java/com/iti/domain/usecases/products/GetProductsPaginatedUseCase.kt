package com.iti.domain.usecases.products

import com.iti.domain.models.PaginatedProducts
import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsPaginatedUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(count: Int = 50, after: String? = null): Flow<Result<PaginatedProducts>> {
        return repository.getProductsPaginated(count, after)
    }
}
