package com.iti.presentation.screens.products.displayallproducts

import com.iti.domain.models.Product
import com.iti.presentation.core.UiText

object AllProductsContract {

    // ─── Sort Options ───────────────────────────────────────────────────────────
    enum class SortOption { BEST_SELLING, PRICE_ASC, PRICE_DESC }

    // ─── Screen States ───────────────────────────────────────────────────────────
    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Success(val products: List<Product>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    // ─── Filter State (what the user has selected) ───────────────────────────────
    data class FilterState(
        val selectedCategory: String? = null,
        val selectedSubCategory: String? = null,
        val selectedBrands: Set<String> = emptySet(),
        val brandSearchQuery: String = ""
    ) {
        val isActive: Boolean
            get() = selectedCategory != null || selectedSubCategory != null || selectedBrands.isNotEmpty()
    }

    // ─── UI State ───────────────────────────────────────────────────────────────
    data class State(
        val screenState: ScreenState = ScreenState.Loading,
        // filtering / sorting
        val activeBrand: String? = null,          // legacy brand from Home navigation
        val searchQuery: String = "",
        val filterState: FilterState = FilterState(),
        val pendingFilterState: FilterState = FilterState(), // in-sheet draft
        val sortOption: SortOption = SortOption.BEST_SELLING,
        val pendingSortOption: SortOption = SortOption.BEST_SELLING, // in-sheet draft
        // available options derived from the full product list
        val availableCategories: List<String> = emptyList(),
        val availableSubCategories: List<String> = emptyList(),
        val availableBrands: List<String> = emptyList(),
        // sheet visibility
        val isSearchActive: Boolean = false,
        val isFilterSheetOpen: Boolean = false,
        val isSortSheetOpen: Boolean = false,
        // recent searches (in-memory)
        val recentSearches: List<String> = emptyList(),
        // pagination
        val hasNextPage: Boolean = false,
        val endCursor: String? = null,
        val isLoadingMore: Boolean = false
    )

    // ─── Intents ─────────────────────────────────────────────────────────────────
    sealed class Intent {
        // load
        data class LoadData(val brandName: String?) : Intent()
        data object Retry : Intent()
        data object LoadMore : Intent()

        // product interaction
        data class ProductClicked(val product: Product) : Intent()
        data class ProductFavoriteClicked(val product: Product) : Intent()

        // legacy brand filter chip
        data object ClearFilter : Intent()

        // direct category chip selection (bypasses pending state)
        data class SelectCategory(val category: String?) : Intent()

        // search
        data object OpenSearch : Intent()
        data object CloseSearch : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class PopularSearchClicked(val term: String) : Intent()
        data class RecentSearchClicked(val term: String) : Intent()
        data object ClearRecentSearches : Intent()

        // filter sheet
        data object OpenFilterSheet : Intent()
        data object CloseFilterSheet : Intent()
        data class PendingCategoryChanged(val category: String?) : Intent()
        data class PendingSubCategoryChanged(val subCategory: String?) : Intent()
        data class PendingBrandToggled(val brand: String) : Intent()
        data class PendingBrandSearchChanged(val query: String) : Intent()
        data object ApplyFilters : Intent()
        data object ResetFilters : Intent()

        // sort sheet
        data object OpenSortSheet : Intent()
        data object CloseSortSheet : Intent()
        data class PendingSortChanged(val option: SortOption) : Intent()
        data object ApplySort : Intent()
    }

    // ─── Effects ─────────────────────────────────────────────────────────────────
    sealed class Effect {
        data class NavigateToProduct(val productId: String) : Effect()
        data object ShowAuthRequired : Effect()
    }
}