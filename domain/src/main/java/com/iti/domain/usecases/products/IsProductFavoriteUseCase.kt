package com.iti.domain.usecases.products

import com.iti.domain.repositories.product.ProductRepository

class IsProductFavoriteUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Boolean {
        return repository.isFavorite(productId)
    }
}