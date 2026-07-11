package com.iti.domain.usecases.products

import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class UpdateProductReviewUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(
        reviewId: String,
        customerName: String,
        customerId: String,
        rating: Int,
        title: String,
        body: String,
        avatarUrl: String? = null
    ): Flow<Result<Unit>> {
        return repository.updateProductReview(reviewId, customerName, customerId, rating, title, body, avatarUrl)
    }
}
