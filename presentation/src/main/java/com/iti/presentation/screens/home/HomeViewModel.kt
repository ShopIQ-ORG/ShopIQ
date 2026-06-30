package com.iti.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.presentation.R
import com.iti.presentation.core.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsByNumberUseCase: GetProductsByNumberUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getAdsUseCase: GetAdsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        sendIntent(HomeContract.Intent.LoadData)
    }

    fun sendIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.LoadData,
            is HomeContract.Intent.Retry -> loadAll()
            is HomeContract.Intent.ProductClicked -> {
                val productId = intent.product.id.substringAfterLast("/").toLong()

                emitEffect(
                    HomeContract.Effect.NavigateToProduct(productId)
                )
            }
            is HomeContract.Intent.ProductFavoriteClicked -> Unit
            is HomeContract.Intent.BrandClicked -> emitEffect(
                HomeContract.Effect.NavigateToProducts(intent.brandName)
            )
            is HomeContract.Intent.ViewAllBrandsClicked -> emitEffect(
                HomeContract.Effect.NavigateToAllBrands()
            )
            is HomeContract.Intent.ViewAllProductsClicked -> emitEffect(
                HomeContract.Effect.NavigateToAllProducts
            )
            is HomeContract.Intent.SearchBarClicked -> emitEffect(
                HomeContract.Effect.NavigateToSearch
            )
            is HomeContract.Intent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            //logoutUseCase()
            emitEffect(HomeContract.Effect.NavigateToSignIn)
        }
    }

    private fun loadAll() {
        _state.update { it.copy(screenState = HomeContract.ScreenState.Loading) }
        viewModelScope.launch {
            combine(
                getProductsByNumberUseCase(),
                getBrandsUseCase(),
                getAdsUseCase()
            ) { productsResult, brandsResult, adsResult ->
                val anyLoading = productsResult is Result.Loading
                        || brandsResult is Result.Loading
                        || adsResult is Result.Loading

                when {
                    anyLoading -> HomeContract.ScreenState.Loading

                    productsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        productsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                    )

                    brandsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        brandsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_brands)
                    )

                    adsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        adsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_ads)
                    )

                    else -> HomeContract.ScreenState.Success(
                        HomeContract.HomeData(
                            products = (productsResult as Result.Success).data,
                            brands = (brandsResult as Result.Success).data,
                            ads = (adsResult as Result.Success).data
                        )
                    )
                }
            }.collect { screenState ->
                _state.update { it.copy(screenState = screenState) }
            }
        }
    }

    private fun emitEffect(effect: HomeContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}