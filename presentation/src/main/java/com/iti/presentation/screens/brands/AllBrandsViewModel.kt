//
//  AllBrandsViewModel.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Brand
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllBrandsViewModel(
    private val getBrandsUseCase: GetBrandsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AllBrandsContract.State())
    val state: StateFlow<AllBrandsContract.State> = _state.asStateFlow()

    private val _effect = Channel<AllBrandsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var allBrands: List<Brand> = emptyList()

    init {
        sendIntent(AllBrandsContract.Intent.LoadData)
    }

    fun sendIntent(intent: AllBrandsContract.Intent) {
        when (intent) {
            is AllBrandsContract.Intent.LoadData,
            is AllBrandsContract.Intent.Retry -> loadBrands()
            is AllBrandsContract.Intent.BrandClicked -> emitEffect(
                AllBrandsContract.Effect.NavigateToProducts(intent.brandName)
            )
            is AllBrandsContract.Intent.QueryChanged -> filterBrands(intent.query)
        }
    }

    private var fetchJob: kotlinx.coroutines.Job? = null

    private fun loadBrands() {
        fetchJob?.cancel()
        _state.update { it.copy(screenState = AllBrandsContract.ScreenState.Loading) }
        fetchJob = viewModelScope.launch {
            getBrandsUseCase().collect { result ->
                _state.update {
                    when (result) {
                        is Result.Loading -> it.copy(
                            screenState = AllBrandsContract.ScreenState.Loading
                        )
                        is Result.Success -> {
                            allBrands = result.data
                            val filtered = applyFilter(result.data, it.query)
                            it.copy(
                                screenState = AllBrandsContract.ScreenState.Success(result.data),
                                filteredBrands = filtered
                            )
                        }
                        is Result.Failure -> it.copy(
                            screenState = AllBrandsContract.ScreenState.Failure(
                                result.exception.message
                                    ?.let { msg -> UiText.Plain(msg) }
                                    ?: UiText.StringResource(R.string.error_loading_brands)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun filterBrands(query: String) {
        val filtered = applyFilter(allBrands, query)
        _state.update {
            it.copy(query = query, filteredBrands = filtered)
        }
    }

    private fun applyFilter(brands: List<Brand>, query: String): List<Brand> {
        if (query.isBlank()) return brands
        return brands.filter { it.name.contains(query, ignoreCase = true) }
    }

    private fun emitEffect(effect: AllBrandsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}