package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.repositories.product.ProductRepository

class AddProductToFavoritesUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.addToFavorites(product)
    }
}