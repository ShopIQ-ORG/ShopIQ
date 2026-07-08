package com.iti.presentation.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.categories.GetCategoriesUseCase
import com.iti.presentation.screens.category.model.CategoryItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

import com.iti.domain.usecases.categories.GetCollectionTranslationsUseCase
import kotlinx.coroutines.flow.update

class CategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCollectionTranslationsUseCase: GetCollectionTranslationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryContract.State())
    val state: StateFlow<CategoryContract.State> = _state.asStateFlow()

    private val _effect = Channel<CategoryContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        sendIntent(CategoryContract.Intent.LoadCategories)
    }

    fun sendIntent(intent: CategoryContract.Intent) {
        when (intent) {
            is CategoryContract.Intent.LoadCategories -> handleLoadCategories()
            is CategoryContract.Intent.SearchQueryChanged -> handleSearchQueryChanged(intent.query)
            is CategoryContract.Intent.CategoryClicked -> handleCategoryClicked(intent.categoryId)
        }
    }

    private fun handleLoadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            errorMessage = null
                        )
                    }
                    is Result.Success -> {
                        val mappedCategories = result.data.map { category ->
                            CategoryItem(
                                id = category.id,
                                title = category.title,
                                imageAssetPath = category.imageAssetPath,
                                arTitle = category.arTitle
                            )
                        }
                        _state.value = _state.value.copy(
                            categories = mappedCategories,
                            isLoading = false,
                            errorMessage = null
                        )
                        if (com.iti.presentation.util.LocaleHelper.isArabic()) {
                            fetchTranslationsForCategories(result.data)
                        }
                    }
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.localizedMessage ?: "Failed to load categories"
                        )
                    }
                }
            }
        }
    }

    private fun fetchTranslationsForCategories(categories: List<com.iti.domain.models.Category>) {
        categories.forEach { category ->
            viewModelScope.launch {
                getCollectionTranslationsUseCase(category.id, "ar").collect { res ->
                    if (res is Result.Success) {
                        val map = res.data
                        val translatedTitle = map?.get("title")
                        if (!translatedTitle.isNullOrBlank()) {
                            _state.update { currentState ->
                                val updatedList = currentState.categories.map { item ->
                                    if (item.id == category.id) {
                                        item.copy(arTitle = translatedTitle)
                                    } else {
                                        item
                                    }
                                }
                                currentState.copy(categories = updatedList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun handleCategoryClicked(categoryId: String) {
        viewModelScope.launch {
            val category = _state.value.categories.find { it.id == categoryId }
            val isArabic = com.iti.presentation.util.LocaleHelper.isArabic()
            val title = if (isArabic) (category?.arTitle ?: category?.title ?: categoryId) else (category?.title ?: categoryId)
            _effect.send(CategoryContract.Effect.NavigateToCategoryProducts(categoryId, title))
        }
    }
}