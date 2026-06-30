package com.iti.presentation.screens.home

import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.presentation.core.UiText

object HomeContract {

    data class HomeData(
        val products: List<Product>,
        val brands: List<Brand>,
        val ads: List<Ad>
    )

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Success(val data: HomeData) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(
        val screenState: ScreenState = ScreenState.Loading
    )

    sealed class Intent {
        data object LoadData : Intent()
        data object Retry : Intent()
        data class ProductFavoriteClicked(val product: Product) : Intent()
        data class ProductClicked(val product: Product) : Intent()
        data class BrandClicked(val brandName: String) : Intent()
        data object ViewAllBrandsClicked : Intent()
        data object ViewAllProductsClicked : Intent()
        data object SearchBarClicked : Intent()
        data object Logout : Intent()
    }

    sealed class Effect {
        data class NavigateToAllBrands(val brandName: String? = null) : Effect()

        data class NavigateToProducts(val brandName: String? = null) : Effect()
        data class NavigateToProduct(val productId: Long) : Effect()
        data object NavigateToAllProducts : Effect()
        data object NavigateToSearch : Effect()
        data object NavigateToSignIn : Effect()
    }
}