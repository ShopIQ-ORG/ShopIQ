package com.iti.data.sources.local.search

import kotlinx.coroutines.flow.Flow

interface SearchHistoryLocalDataSource {
    fun getSearchHistory(): Flow<List<String>>
    suspend fun addSearchQuery(query: String)
    suspend fun deleteSearchQuery(query: String)
    suspend fun clearSearchHistory()
}
