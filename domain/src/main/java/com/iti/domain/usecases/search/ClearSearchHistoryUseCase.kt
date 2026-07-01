package com.iti.domain.usecases.search

import com.iti.domain.repositories.search.SearchHistoryRepository

class ClearSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke() {
        repository.clearSearchHistory()
    }
}
