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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
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
            is CategoryContract.Intent.CategoryClicked -> handleCategoryClicked(intent.categoryName)
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
                                itemCount = category.itemCount,
                                imageAssetPath = category.imageAssetPath
                            )
                        }
                        _state.value = _state.value.copy(
                            categories = mappedCategories,
                            isLoading = false,
                            errorMessage = null
                        )
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

    private fun handleSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun handleCategoryClicked(categoryName: String) {
        viewModelScope.launch {
            _effect.send(CategoryContract.Effect.NavigateToCategoryProducts(categoryName))
        }
    }
}
