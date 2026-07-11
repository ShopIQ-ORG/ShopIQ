package com.iti.domain.usecases.search

import com.iti.domain.repositories.product.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getSearchHistory()
    }
}
