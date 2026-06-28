package com.iti.presentation.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.presentation.screens.category.model.CategoryItem
import com.iti.presentation.util.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        CategoryContract.State(
            categories = getInitialCategories()
        )
    )
    val state: StateFlow<CategoryContract.State> = _state.asStateFlow()

    private val _effect = Channel<CategoryContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun sendIntent(intent: CategoryContract.Intent) {
        when (intent) {
            is CategoryContract.Intent.SearchQueryChanged -> handleSearchQueryChanged(intent.query)
            is CategoryContract.Intent.CategoryClicked -> handleCategoryClicked(intent.categoryId)
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

    private fun getInitialCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                id = "men",
                title = Constants.CATEGORY_MEN,
                itemCount = Constants.CATEGORY_MEN_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_1
            ),
            CategoryItem(
                id = "women",
                title = Constants.CATEGORY_WOMEN,
                itemCount = Constants.CATEGORY_WOMEN_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_2
            ),
            CategoryItem(
                id = "shoes",
                title = Constants.CATEGORY_SHOES,
                itemCount = Constants.CATEGORY_SHOES_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_3
            ),
            CategoryItem(
                id = "bags",
                title = Constants.CATEGORY_BAGS,
                itemCount = Constants.CATEGORY_BAGS_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_1
            ),
            CategoryItem(
                id = "accessories",
                title = Constants.CATEGORY_ACCESSORIES,
                itemCount = Constants.CATEGORY_ACCESSORIES_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_2
            ),
            CategoryItem(
                id = "beauty",
                title = Constants.CATEGORY_BEAUTY,
                itemCount = Constants.CATEGORY_BEAUTY_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_3
            ),
            CategoryItem(
                id = "kids",
                title = Constants.CATEGORY_KIDS,
                itemCount = Constants.CATEGORY_KIDS_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_1
            ),
            CategoryItem(
                id = "sale",
                title = Constants.CATEGORY_SALE,
                itemCount = Constants.CATEGORY_SALE_COUNT,
                imageAssetPath = Constants.ONBOARDING_IMAGE_2
            )
        )
    }
}
