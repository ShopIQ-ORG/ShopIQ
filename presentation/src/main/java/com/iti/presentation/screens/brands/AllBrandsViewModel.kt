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

import com.iti.domain.usecases.categories.GetCollectionTranslationsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first

class AllBrandsViewModel(
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getCollectionTranslationsUseCase: GetCollectionTranslationsUseCase
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
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(screenState = AllBrandsContract.ScreenState.Loading) }
                    }
                    is Result.Success -> {
                        allBrands = result.data
                        val filtered = applyFilter(result.data, _state.value.query)
                        _state.update {
                            it.copy(
                                screenState = AllBrandsContract.ScreenState.Success(result.data),
                                filteredBrands = filtered
                            )
                        }
                        if (com.iti.presentation.util.LocaleHelper.isArabic()) {
                            fetchTranslationsForBrands(result.data)
                        }
                    }
                    is Result.Failure -> {
                        _state.update {
                            it.copy(
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
    }

    private fun fetchTranslationsForBrands(brands: List<Brand>) {
        viewModelScope.launch {
            val translatedMap = brands.distinctBy { it.id }.map { brand ->
                async {
                    try {
                        val res = getCollectionTranslationsUseCase(brand.id, "ar")
                            .first { it !is Result.Loading }
                        if (res is Result.Success) {
                            val map = res.data
                            brand.id to brand.copy(
                                arTitle = map?.get("title")?.takeIf { it.isNotBlank() }
                            )
                        } else brand.id to brand
                    } catch (_: Exception) {
                        brand.id to brand
                    }
                }
            }.awaitAll().toMap()

            allBrands = allBrands.map { translatedMap[it.id] ?: it }
            val filtered = applyFilter(allBrands, _state.value.query)
            _state.update {
                it.copy(
                    screenState = AllBrandsContract.ScreenState.Success(allBrands),
                    filteredBrands = filtered
                )
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
        return brands.filter {
            it.name.contains(query, ignoreCase = true) ||
                    (it.arTitle?.contains(query, ignoreCase = true) == true)
        }
    }

    private fun emitEffect(effect: AllBrandsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}