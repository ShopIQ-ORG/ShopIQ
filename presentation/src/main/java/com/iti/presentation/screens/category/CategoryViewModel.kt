package com.iti.presentation.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.categories.GetCategoriesUseCase
import com.iti.presentation.screens.category.model.CategoryItem
import com.iti.presentation.util.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
            is CategoryContract.Intent.SearchQueryChanged -> handleSearchQueryChanged(intent.query)
            is CategoryContract.Intent.CategoryClicked -> handleCategoryClicked(intent.categoryId)
            is CategoryContract.Intent.LoadCategories -> loadCategories()
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    private fun handleCategoryClicked(categoryId: String) {
        viewModelScope.launch {
            _effect.send(CategoryContract.Effect.NavigateToCategoryProducts(categoryId))
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Result.Success -> {
                        val mappedItems = result.data.mapIndexed { index, category ->
                            val fallbackImage = when (category.title.uppercase()) {
                                "MEN" -> Constants.ONBOARDING_IMAGE_1
                                "WOMEN" -> Constants.ONBOARDING_IMAGE_2
                                "KID", "KIDS" -> Constants.ONBOARDING_IMAGE_3
                                else -> when (index % 3) {
                                    0 -> Constants.ONBOARDING_IMAGE_1
                                    1 -> Constants.ONBOARDING_IMAGE_2
                                    else -> Constants.ONBOARDING_IMAGE_3
                                }
                            }
                            CategoryItem(
                                id = category.id,
                                title = category.title,
                                itemCount = category.productsCount,
                                imageAssetPath = category.imageUrl?.takeIf { it.isNotEmpty() } ?: fallbackImage
                            )
                        }
                        _state.value = _state.value.copy(
                            categories = mappedItems,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = result.exception.message ?: "Failed to load categories"
                        )
                    }
                }
            }
        }
    }
}
