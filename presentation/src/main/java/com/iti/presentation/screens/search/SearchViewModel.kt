//
//  SearchViewModel.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetPopularProductsUseCase
import com.iti.domain.usecases.products.SearchProductsUseCase
import com.iti.domain.usecases.search.AddSearchQueryUseCase
import com.iti.domain.usecases.search.ClearSearchHistoryUseCase
import com.iti.domain.usecases.search.DeleteSearchQueryUseCase
import com.iti.domain.usecases.search.GetSearchHistoryUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getPopularProductsUseCase: GetPopularProductsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchQueryUseCase: AddSearchQueryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchContract.State())
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()

    private val _effect = Channel<SearchContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        sendIntent(SearchContract.Intent.LoadInitialData)
        observeSearchHistory()
    }

    fun sendIntent(intent: SearchContract.Intent) {
        when (intent) {
            SearchContract.Intent.LoadInitialData -> {
                loadPopularProducts()
                _state.update {
                    it.copy(
                        trendingSearches = listOf(
                            "Nike sneakers",
                            "Summer dress",
                            "Leather jacket",
                            "Yoga pants",
                            "Adidas running"
                        )
                    )
                }
            }
            is SearchContract.Intent.QueryChanged -> {
                val newQuery = intent.query
                _state.update { it.copy(query = newQuery) }
                if (newQuery.isBlank()) {
                    searchJob?.cancel()
                    _state.update { it.copy(screenState = SearchContract.ScreenState.Empty) }
                } else {
                    performDebouncedSearch(newQuery)
                }
            }
            is SearchContract.Intent.SearchSubmitted -> {
                val query = intent.query
                if (query.isNotBlank()) {
                    searchJob?.cancel()
                    saveSearchQuery(query)
                    performImmediateSearch(query)
                }
            }
            is SearchContract.Intent.DeleteRecentSearch -> {
                viewModelScope.launch {
                    deleteSearchQueryUseCase(intent.query)
                }
            }
            SearchContract.Intent.ClearAllRecentSearches -> {
                viewModelScope.launch {
                    clearSearchHistoryUseCase()
                }
            }
            is SearchContract.Intent.ProductClicked -> {
                val productId = intent.product.id.substringAfterLast("/").toLong()
                emitEffect(SearchContract.Effect.NavigateToProduct(productId))
            }
        }
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            getSearchHistoryUseCase().collect { history ->
                _state.update { it.copy(recentSearches = history) }
            }
        }
    }

    private fun loadPopularProducts() {
        viewModelScope.launch {
            getPopularProductsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Silent loading in background for suggestions
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popularProducts = result.data) }
                    }
                    is Result.Failure -> {
                        // Fail silently or log
                    }
                }
            }
        }
    }

    private fun performDebouncedSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _state.update { it.copy(screenState = SearchContract.ScreenState.Loading) }
            searchProductsUseCase(query).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(screenState = SearchContract.ScreenState.Loading) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(screenState = SearchContract.ScreenState.Suggestions(result.data))
                        }
                    }
                    is Result.Failure -> {
                        val errorMsg = result.exception.message?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                        _state.update {
                            it.copy(screenState = SearchContract.ScreenState.Failure(errorMsg))
                        }
                    }
                }
            }
        }
    }

    private fun performImmediateSearch(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(screenState = SearchContract.ScreenState.Loading, query = query) }
            searchProductsUseCase(query).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(screenState = SearchContract.ScreenState.Loading) }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(screenState = SearchContract.ScreenState.Success(result.data))
                        }
                    }
                    is Result.Failure -> {
                        val errorMsg = result.exception.message?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                        _state.update {
                            it.copy(screenState = SearchContract.ScreenState.Failure(errorMsg))
                        }
                    }
                }
            }
        }
    }

    private fun saveSearchQuery(query: String) {
        viewModelScope.launch {
            addSearchQueryUseCase(query)
        }
    }

    private fun emitEffect(effect: SearchContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
