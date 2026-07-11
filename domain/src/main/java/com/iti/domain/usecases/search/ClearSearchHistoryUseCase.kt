package com.iti.domain.usecases.search

import com.iti.domain.repositories.product.ProductRepository

class ClearSearchHistoryUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() {
        repository.clearSearchHistory()
    }
}
