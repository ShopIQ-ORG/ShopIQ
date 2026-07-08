package com.iti.domain.usecases.products

import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

class UpdateProductReviewUseCase(
    private val repository: ProductsRepository
) {
    operator fun invoke(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        avatarUrl: String? = null
    ): Flow<Result<Unit>> {
        return repository.updateProductReview(reviewId, customerName, rating, title, body, avatarUrl)
    }
}
