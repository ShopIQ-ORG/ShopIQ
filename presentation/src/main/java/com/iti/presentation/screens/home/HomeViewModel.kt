package com.iti.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsByNumberUseCase: GetProductsByNumberUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getAdsUseCase: GetAdsUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<Result<List<Product>>>(Result.Loading)
    val products: StateFlow<Result<List<Product>>> = _products.asStateFlow()

    private val _brands = MutableStateFlow<Result<List<Brand>>>(Result.Loading)
    val brands: StateFlow<Result<List<Brand>>> = _brands.asStateFlow()

    private val _ads = MutableStateFlow<Result<List<Ad>>>(Result.Loading)
    val ads: StateFlow<Result<List<Ad>>> = _ads.asStateFlow()

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        fetchProducts()
        fetchBrands()
        fetchAds()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            getProductsByNumberUseCase().collect {
                _products.value = it
            }
        }
    }

    private fun fetchBrands() {
        viewModelScope.launch {
            getBrandsUseCase().collect {
                _brands.value = it
            }
        }
    }

    private fun fetchAds() {
        viewModelScope.launch {
            getAdsUseCase().collect {
                _ads.value = it
            }
        }
    }
}
