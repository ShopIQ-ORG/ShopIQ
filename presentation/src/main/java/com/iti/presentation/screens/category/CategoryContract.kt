package com.iti.presentation.screens.category

import com.iti.presentation.screens.category.model.CategoryItem

interface CategoryContract {
    data class State(
        val categories: List<CategoryItem> = emptyList(),
        val searchQuery: String = ""
    )

    sealed interface Intent {
        data class SearchQueryChanged(val query: String) : Intent
        data class CategoryClicked(val categoryId: String) : Intent
    }

    sealed interface Effect {
        data class NavigateToCategoryProducts(val categoryId: String) : Effect
    }
}
