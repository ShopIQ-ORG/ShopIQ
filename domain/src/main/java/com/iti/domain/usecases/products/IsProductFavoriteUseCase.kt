package com.iti.domain.usecases.products

import com.iti.domain.repositories.products.ProductsRepository

class IsProductFavoriteUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: String): Boolean {
        return repository.isFavorite(productId)
    }
}