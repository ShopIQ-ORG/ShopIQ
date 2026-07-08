package com.iti.domain.usecases.products

import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

class AddProductReviewUseCase(
    private val repository: ProductsRepository
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
