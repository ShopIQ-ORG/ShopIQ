package com.iti.data.repositories

import com.iti.data.sources.local.search.SearchHistoryLocalDataSource
import com.iti.domain.repositories.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val localDataSource: SearchHistoryLocalDataSource
) : SearchHistoryRepository {

    override fun getSearchHistory(): Flow<List<String>> {
        return localDataSource.getSearchHistory()
    }

    override suspend fun addSearchQuery(query: String) {
        localDataSource.addSearchQuery(query)
    }

    override suspend fun deleteSearchQuery(query: String) {
        localDataSource.deleteSearchQuery(query)
    }

    override suspend fun clearSearchHistory() {
        localDataSource.clearSearchHistory()
    }
}
