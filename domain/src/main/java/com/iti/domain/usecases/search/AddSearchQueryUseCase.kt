package com.iti.domain.usecases.search

import com.iti.domain.repositories.product.ProductRepository

class AddSearchQueryUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(query: String) {
        repository.addSearchQuery(query)
    }
}
