package com.iti.presentation.screens.home

import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.presentation.util.UiText

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
        val screenState: ScreenState = ScreenState.Loading,
        val currentUser: User? = null,
        val aiRecommendedProducts: List<Product> = emptyList(),
        val hasChatHistory: Boolean = false,
        val isLoadingRecommendations: Boolean = false,
        val isFavoriteLoading: Boolean = false
    )
    data class MainDataHolder(
        val productsResult: Result<List<Product>>,
        val brandsResult: Result<List<Brand>>,
        val adsResult: Result<List<Ad>>,
        val favoritesResult: Result<List<Product>>,
        val overrides: Map<String, Boolean>
    )

    sealed class Intent {
        data object LoadData : Intent()
        data object Retry : Intent()
        data class ProductFavoriteClicked(val product: Product) : Intent()
        data class ProductClicked(val product: Product) : Intent()
        data class AiRecommendedProductClicked(val product: Product) : Intent()
        data class BrandClicked(val brandName: String) : Intent()
        data class AdClicked(val ad: Ad) : Intent()
        data object ViewAllBrandsClicked : Intent()
        data object ViewAllProductsClicked : Intent()
        data object SearchBarClicked : Intent()
        data object NavigateToAiChat : Intent()
        data object Logout : Intent()
    }

    sealed class Effect {
        data class NavigateToAllBrands(val brandName: String? = null) : Effect()
        data class NavigateToProducts(val brandName: String? = null) : Effect()
        data class NavigateToProduct(val productId: Long) : Effect()
        data object NavigateToAllProducts : Effect()
        data object ShowAuthRequired : Effect()
        data object NavigateToSearch : Effect()
        data object NavigateToSignIn : Effect()
        data object NavigateToAiChat : Effect()
        data class ShowToast(val message: UiText) : Effect()
    }
}