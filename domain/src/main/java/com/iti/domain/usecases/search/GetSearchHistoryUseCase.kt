package com.iti.domain.usecases.search

import com.iti.domain.repositories.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getSearchHistory()
    }
}
