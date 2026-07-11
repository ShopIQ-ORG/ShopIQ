package com.iti.domain.usecases.products

import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductTranslationsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(productId: String): Flow<Result<Map<String, String>>> {
        return repository.getProductTranslations(productId)
    }
}
