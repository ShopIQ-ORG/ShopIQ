package com.iti.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iti.data.utils.handleException
import com.iti.domain.repositories.search.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.NonCancellable

class SearchHistoryRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : SearchHistoryRepository {

    private object PreferencesKeys {
        val SEARCH_HISTORY = stringPreferencesKey("search_history")
    }

    override fun getSearchHistory(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val json = preferences[PreferencesKeys.SEARCH_HISTORY] ?: ""
            if (json.isEmpty()) {
                emptyList()
            } else {
                try {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson(json, type) ?: emptyList()
                } catch (e: Exception) {
                    e.handleException()
                    emptyList()
                }
            }
        }
    }

    override suspend fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        withContext(NonCancellable) {
            dataStore.edit { preferences ->
                val json = preferences[PreferencesKeys.SEARCH_HISTORY] ?: ""
                val currentList = if (json.isEmpty()) {
                    emptyList()
                } else {
                    try {
                        val type = object : TypeToken<List<String>>() {}.type
                        gson.fromJson<List<String>>(json, type) ?: emptyList()
                    } catch (e: Exception) {
                        e.handleException()
                        emptyList()
                    }
                }.toMutableList()

                currentList.remove(query)
                currentList.add(0, query)

                if (currentList.size > 5) {
                    currentList.removeAt(currentList.lastIndex)
                }

                preferences[PreferencesKeys.SEARCH_HISTORY] = gson.toJson(currentList)
            }
        }
    }

    override suspend fun deleteSearchQuery(query: String) {
        withContext(NonCancellable) {
            dataStore.edit { preferences ->
                val json = preferences[PreferencesKeys.SEARCH_HISTORY] ?: ""
                if (json.isNotEmpty()) {
                    val currentList = try {
                        val type = object : TypeToken<List<String>>() {}.type
                        gson.fromJson<List<String>>(json, type) ?: emptyList()
                    } catch (e: Exception) {
                        e.handleException()
                        emptyList()
                    }.toMutableList()

                    if (currentList.remove(query)) {
                        preferences[PreferencesKeys.SEARCH_HISTORY] = gson.toJson(currentList)
                    }
                }
            }
        }
    }

    override suspend fun clearSearchHistory() {
        withContext(NonCancellable) {
            dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.SEARCH_HISTORY)
            }
        }
    }
}
