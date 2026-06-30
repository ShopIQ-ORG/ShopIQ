package com.iti.domain.usecases.search

import com.iti.domain.repositories.search.SearchHistoryRepository

class AddSearchQueryUseCase(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        repository.addSearchQuery(query)
    }
}
