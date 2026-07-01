package com.iti.domain.usecases.products

import com.iti.domain.models.Product
import com.iti.domain.repositories.products.ProductsRepository

class AddProductToFavoritesUseCase(
    private val repository: ProductsRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.addToFavorites(product)
    }
}