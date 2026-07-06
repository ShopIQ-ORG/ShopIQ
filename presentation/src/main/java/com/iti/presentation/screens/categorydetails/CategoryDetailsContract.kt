package com.iti.presentation.screens.categorydetails

import com.iti.domain.models.Product
import com.iti.presentation.util.UiText

interface CategoryDetailsContract {
    data class State(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val categoryId: String? = null
    )

    sealed interface Intent {
        data class LoadProducts(val categoryId: String) : Intent
        data class ProductFavoriteClicked(val product: Product) : Intent
    }

    sealed interface Effect {
        data class ShowSnackbar(val message: UiText.StringResource) : Effect
        data object ShowAuthRequired : Effect
    }
}
