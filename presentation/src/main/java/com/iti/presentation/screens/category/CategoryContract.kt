package com.iti.presentation.screens.category

import com.iti.presentation.screens.category.model.CategoryItem

interface CategoryContract {
    data class State(
        val categories: List<CategoryItem> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Intent {
        data object LoadCategories : Intent
        data class SearchQueryChanged(val query: String) : Intent
        data class CategoryClicked(val categoryName: String) : Intent
    }

    sealed interface Effect {
        data class NavigateToCategoryProducts(val categoryName: String) : Effect
    }
}
