//
//  AllBrandsContract.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands

import com.iti.domain.models.Brand
import com.iti.presentation.util.UiText

object AllBrandsContract {

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Success(val brands: List<Brand>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(
        val screenState: ScreenState = ScreenState.Loading,
        val query: String = "",
        val filteredBrands: List<Brand> = emptyList()
    )

    sealed class Intent {
        data object LoadData : Intent()
        data object Retry : Intent()
        data class BrandClicked(val brandName: String, val displayTitle: String? = null) : Intent()
        data class QueryChanged(val query: String) : Intent()
    }

    sealed class Effect {
        data class NavigateToProducts(val brandName: String, val displayTitle: String? = null) : Effect()
    }
}