package com.iti.domain.usecases.products

import com.iti.domain.repositories.products.ProductsRepository

class RemoveProductFromFavoritesUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(productId: String) {
        repository.removeFromFavorites(productId)
    }
}