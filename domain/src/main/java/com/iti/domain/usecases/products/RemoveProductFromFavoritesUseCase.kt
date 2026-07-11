package com.iti.domain.usecases.products

import com.iti.domain.repositories.product.ProductRepository

class RemoveProductFromFavoritesUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(productId: String) {
        repository.removeFromFavorites(productId)
    }
}