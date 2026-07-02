package com.iti.presentation.screens.categorydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.categories.GetProductsByCategoryUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryDetailsViewModel(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryDetailsContract.State())
    val state: StateFlow<CategoryDetailsContract.State> = _state.asStateFlow()

    private val _effect = Channel<CategoryDetailsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val favoriteOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val _products = MutableStateFlow<List<Product>>(emptyList())

    init {
        observeProductsWithFavorites()
    }

    fun sendIntent(intent: CategoryDetailsContract.Intent) {
        when (intent) {
            is CategoryDetailsContract.Intent.LoadProducts -> loadProducts(intent.categoryId)
            is CategoryDetailsContract.Intent.ProductFavoriteClicked -> toggleFavorite(intent.product)
        }
    }

    private fun observeProductsWithFavorites() {
        viewModelScope.launch {
            combine(
                _products,
                getFavoriteProductsUseCase(),
                favoriteOverrides,
                ::mergeFavoriteState
            ).collect { updatedProducts ->
                _state.update { it.copy(products = updatedProducts) }
            }
        }
    }

    private fun mergeFavoriteState(
        products: List<Product>,
        favoritesResult: Result<List<Product>>,
        overrides: Map<String, Boolean>
    ): List<Product> {
        val favoriteIds = (favoritesResult as? Result.Success)?.data?.map { it.id }?.toSet().orEmpty()
        return products.map { product ->
            val isFavoriteInDb = product.id in favoriteIds
            product.copy(isFavorite = overrides[product.id] ?: isFavoriteInDb)
        }
    }

    private fun loadProducts(categoryId: String) {
        if (isAlreadyLoaded(categoryId)) return

        viewModelScope.launch {
            beginLoading(categoryId)
            getProductsByCategoryUseCase(categoryId).collect { result ->
                handleLoadResult(categoryId, result)
            }
        }
    }

    private fun isAlreadyLoaded(categoryId: String): Boolean {
        val current = _state.value
        return current.categoryId == categoryId &&
                current.errorMessage == null &&
                _products.value.isNotEmpty()
    }

    private fun beginLoading(categoryId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null, categoryId = categoryId) }
    }

    private fun handleLoadResult(categoryId: String, result: Result<List<Product>>) {
        when (result) {
            is Result.Loading -> _state.update { it.copy(isLoading = true, errorMessage = null) }
            is Result.Success -> onProductsLoaded(categoryId, result.data)
            is Result.Failure -> onLoadFailed(result.exception)
        }
    }

    private fun onProductsLoaded(categoryId: String, products: List<Product>) {
        _products.value = products
        _state.update { it.copy(isLoading = false, errorMessage = null, categoryId = categoryId) }
    }

    private fun onLoadFailed(exception: Throwable) {
        _state.update { it.copy(isLoading = false, errorMessage = resolveErrorMessage(exception)) }
    }

    private fun resolveErrorMessage(exception: Throwable): String =
        exception.localizedMessage ?: "Failed to load products"

    private fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            if (!isUserLoggedIn()) {
                notifyLoginRequired()
                return@launch
            }

            val newFavoriteState = !product.isFavorite
            applyFavoriteOverride(product.id, newFavoriteState)
            persistFavoriteChange(product, newFavoriteState)
        }
    }

    private suspend fun isUserLoggedIn(): Boolean = getCurrentUserUseCase() is Result.Success

    private suspend fun notifyLoginRequired() {
        _effect.send(CategoryDetailsContract.Effect.ShowSnackbar("Please log in to add favorites"))
    }

    private fun applyFavoriteOverride(productId: String, isFavorite: Boolean) {
        favoriteOverrides.update { it + (productId to isFavorite) }
    }

    private fun revertFavoriteOverride(productId: String) {
        favoriteOverrides.update { it - productId }
    }

    private suspend fun persistFavoriteChange(product: Product, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                addProductToFavoritesUseCase(product)
            } else {
                removeProductFromFavoritesUseCase(product.id)
            }
        } catch (e: Exception) {
            revertFavoriteOverride(product.id)
            _effect.send(CategoryDetailsContract.Effect.ShowSnackbar("Failed to update favorite status"))
        }
    }
}