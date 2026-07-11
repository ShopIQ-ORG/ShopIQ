package com.iti.domain.usecases.products

import com.iti.domain.models.Result
import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class AddProductReviewUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(
        productId: String,
        customerName: String,
        customerId: String,
        rating: Int,
        title: String,
        body: String,
        avatarUrl: String? = null
    ): Flow<Result<Unit>> {
        return repository.addProductReview(
            productId,
            customerName,
            customerId,
            rating,
            title,
            body,
            avatarUrl
        )
    }
}
