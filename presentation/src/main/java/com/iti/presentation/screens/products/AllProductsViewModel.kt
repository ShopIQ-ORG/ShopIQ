package com.iti.presentation.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.presentation.R
import com.iti.presentation.core.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllProductsViewModel(
    private val getProductsByNumberUseCase: GetProductsByNumberUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AllProductsContract.State())
    val state: StateFlow<AllProductsContract.State> = _state.asStateFlow()

    private val _effect = Channel<AllProductsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var allProducts: List<com.iti.domain.models.Product> = emptyList()

    fun sendIntent(intent: AllProductsContract.Intent) {
        when (intent) {
            is AllProductsContract.Intent.LoadData -> load(intent.brandName)
            is AllProductsContract.Intent.Retry -> load(_state.value.activeBrand)
            is AllProductsContract.Intent.ClearFilter -> applyFilter(null)
            is AllProductsContract.Intent.ProductClicked -> emitEffect(
                AllProductsContract.Effect.NavigateToProduct(intent.product.id)
            )
            is AllProductsContract.Intent.ProductFavoriteClicked -> Unit
        }
    }

    private fun load(brandName: String?) {
        _state.update { it.copy(screenState = AllProductsContract.ScreenState.Loading, activeBrand = brandName) }
        viewModelScope.launch {
            getProductsByNumberUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> _state.update {
                        it.copy(screenState = AllProductsContract.ScreenState.Loading)
                    }
                    is Result.Success -> {
                        allProducts = result.data
                        applyFilter(brandName)
                    }
                    is Result.Failure -> _state.update {
                        it.copy(
                            screenState = AllProductsContract.ScreenState.Failure(
                                result.exception.message
                                    ?.let { msg -> UiText.Plain(msg) }
                                    ?: UiText.StringResource(R.string.error_loading_products)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun applyFilter(brandName: String?) {
        val filtered = if (brandName != null) {
            allProducts.filter { it.vendor.equals(brandName, ignoreCase = true) }
        } else {
            allProducts
        }
        _state.update {
            it.copy(
                screenState = AllProductsContract.ScreenState.Success(filtered),
                activeBrand = brandName
            )
        }
    }

    private fun emitEffect(effect: AllProductsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}