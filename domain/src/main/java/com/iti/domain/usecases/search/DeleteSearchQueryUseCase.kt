package com.iti.domain.usecases.search

import com.iti.domain.repositories.product.ProductRepository

class DeleteSearchQueryUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(query: String) {
        repository.deleteSearchQuery(query)
    }
}
