package com.iti.presentation.screens.products

import com.iti.domain.models.Product
import com.iti.presentation.core.UiText

object AllProductsContract {

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Success(val products: List<Product>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(
        val screenState: ScreenState = ScreenState.Loading,
        val activeBrand: String? = null
    )

    sealed class Intent {
        data class LoadData(val brandName: String?) : Intent()
        data object Retry : Intent()
        data object ClearFilter : Intent()
        data class ProductClicked(val product: Product) : Intent()
        data class ProductFavoriteClicked(val product: Product) : Intent()
    }

    sealed class Effect {
        data class NavigateToProduct(val productId: String) : Effect()
    }
}