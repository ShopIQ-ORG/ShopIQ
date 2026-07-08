package com.iti.domain.usecases.products

import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

class DeleteProductReviewUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(
        productId: String,
        reviewId: String
    ): Flow<Result<Unit>> {
        return repository.deleteProductReview(productId, reviewId)
    }
}
