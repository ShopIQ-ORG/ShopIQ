//
//  SearchContract.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.search

import com.iti.domain.models.Product
import com.iti.presentation.util.UiText

object SearchContract {

    sealed class ScreenState {
        data object Empty : ScreenState()
        data object Loading : ScreenState()
        data class Suggestions(val products: List<Product>) : ScreenState()
        data class Success(val products: List<Product>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(
        val query: String = "",
        val screenState: ScreenState = ScreenState.Empty,
        val recentSearches: List<String> = emptyList(),
        val trendingSearches: List<String> = emptyList(),
        val popularProducts: List<Product> = emptyList()
    )

    sealed class Intent {
        data object LoadInitialData : Intent()
        data class QueryChanged(val query: String) : Intent()
        data class SearchSubmitted(val query: String) : Intent()
        data class DeleteRecentSearch(val query: String) : Intent()
        data object ClearAllRecentSearches : Intent()
        data class ProductClicked(val product: Product) : Intent()
    }

    sealed class Effect {
        data class NavigateToProduct(val productId: Long) : Effect()
        data object NavigateBack : Effect()
    }
}
