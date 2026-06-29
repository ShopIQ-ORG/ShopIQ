package com.iti.presentation.screens.brands

import com.iti.domain.models.Brand
import com.iti.presentation.core.UiText

object AllBrandsContract {

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Success(val brands: List<Brand>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(val screenState: ScreenState = ScreenState.Loading)

    sealed class Intent {
        data object LoadData : Intent()
        data object Retry : Intent()
        data class BrandClicked(val brandName: String) : Intent()
    }

    sealed class Effect {
        data class NavigateToProducts(val brandName: String) : Effect()
    }
}