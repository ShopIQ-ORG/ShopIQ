package com.iti.presentation.screens.category

import com.iti.presentation.screens.category.model.CategoryItem
import com.iti.presentation.util.UiText

interface CategoryContract {
    data class State(
        val categories: List<CategoryItem> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val errorMessage: UiText? = null
    )

    sealed interface Intent {
        data object LoadCategories : Intent
        data class SearchQueryChanged(val query: String) : Intent
        data class CategoryClicked(val categoryId: String) : Intent
    }

    sealed interface Effect {
        data class NavigateToCategoryProducts(val categoryId: String, val categoryTitle: String) : Effect
    }
}

