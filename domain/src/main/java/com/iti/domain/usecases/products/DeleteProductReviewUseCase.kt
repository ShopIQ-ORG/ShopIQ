package com.iti.domain.usecases.products

import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class DeleteProductReviewUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(
        productId: String,
        reviewId: String
    ): Flow<Result<Unit>> {
        return repository.deleteProductReview(productId, reviewId)
    }
}
