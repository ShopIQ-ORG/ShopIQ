package com.iti.presentation.screens.products.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.cart.AddCartItemUseCase
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import com.iti.presentation.util.UiText
import com.iti.presentation.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val addToCartUseCase: AddCartItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsUiState())
    val state: StateFlow<ProductDetailsUiState> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProductDetailsSideEffect>()
    val sideEffects: SharedFlow<ProductDetailsSideEffect> = _sideEffects.asSharedFlow()

    private val favoriteOverride = MutableStateFlow<Boolean?>(null)

    fun handleIntent(intent: ProductDetailsIntent) {
        when (intent) {
            is ProductDetailsIntent.LoadProductDetails -> loadProductDetails(intent.productId)
            is ProductDetailsIntent.SelectColor -> selectColor(intent.color)
            is ProductDetailsIntent.SelectSize -> selectSize(intent.size)
            is ProductDetailsIntent.SelectImage -> selectImage(intent.index)
            is ProductDetailsIntent.ToggleWishlist -> toggleWishlist()
            is ProductDetailsIntent.AddToCart -> addToCart()
            is ProductDetailsIntent.DismissUnauthorizedDialog -> dismissUnauthorizedDialog()
        }
    }

    private fun loadProductDetails(productId: Long) {
        viewModelScope.launch {
            val userRes = getCurrentUserUseCase()
            if (userRes is Result.Success && userRes.data is User.AuthenticatedUser) {
                observeFavoriteStatus(productId.toString())
            }
        }
        viewModelScope.launch {
            getProductDetailsUseCase(productId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoading = true, product = null, error = null) }
                    }
                    is Result.Success -> {
                        val product = result.data
                        _state.update {
                            it.copy(
                                isLoading = false,
                                product = product,
                                selectedColor = "Beige",
                                selectedSize = "M",
                                selectedImageIndex = 0
                            )
                        }
                    }
                    is Result.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.message ?: "Unknown error"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteStatus(productId: String) {
        val cleanProductId = productId.substringAfterLast("/")

        viewModelScope.launch {
            combine(
                getFavoriteProductsUseCase(),
                favoriteOverride
            ) { result: Result<List<com.iti.domain.models.Product>>, override: Boolean? ->
                if (result is Result.Success) {
                    val isFavoriteInDb = result.data.any {
                        it.id.substringAfterLast("/") == cleanProductId
                    }
                    _state.update { it.copy(isWishlisted = override ?: isFavoriteInDb) }
                }
            }.collect()
        }
    }

    private fun selectColor(color: String) {
        _state.update { it.copy(selectedColor = color) }
    }

    private fun selectSize(size: String) {
        _state.update { it.copy(selectedSize = size) }
    }

    private fun selectImage(index: Int) {
        _state.update { it.copy(selectedImageIndex = index) }
    }

    private fun toggleWishlist() {
        viewModelScope.launch {
            val userRes = getCurrentUserUseCase()
            if (userRes is Result.Success && userRes.data is User.AuthenticatedUser) {
                val currentProduct = _state.value.product ?: return@launch
                val currentlyWishlisted = _state.value.isWishlisted

                try {
                    val newStatus = !currentlyWishlisted
                    favoriteOverride.value = newStatus

                    if (currentlyWishlisted) {
                        removeProductFromFavoritesUseCase(currentProduct.id)
                    } else {
                        addProductToFavoritesUseCase(currentProduct)
                    }

                    val message = if (newStatus) {
                        UiText.StringResource(R.string.added_to_wishlist)
                    } else {
                        UiText.StringResource(R.string.removed_from_wishlist)
                    }
                    _sideEffects.emit(ProductDetailsSideEffect.ShowSnackbar(message = message, kind = SnackbarKind.Success))

                    kotlinx.coroutines.delay(1000)
                    favoriteOverride.value = null
                } catch (e: Exception) {
                    favoriteOverride.value = null
                    _sideEffects.emit(
                        ProductDetailsSideEffect.ShowSnackbar(
                            message = UiText.Plain("Error: ${e.message}"),
                            kind = SnackbarKind.Error
                        )
                    )
                }
            } else {
                _state.update { it.copy(showUnauthorizedDialog = true) }
            }
        }
    }

    private fun addToCart() {
        if (_state.value.isAddingToCart) return

        val variantId = selectedVariantId()
        if (variantId == null) {
            viewModelScope.launch {
                _sideEffects.emit(
                    ProductDetailsSideEffect.ShowSnackbar(
                        message = UiText.Plain("Please select options first"),
                        kind = SnackbarKind.Error
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }

            when (val result = addToCartUseCase(variantId = variantId, quantity = 1)) {
                is Result.Success -> {
                    _state.update { it.copy(isAddingToCart = false) }
                    _sideEffects.emit(
                        ProductDetailsSideEffect.ShowSnackbar(
                            message = UiText.Plain("Added to Cart!"),
                            kind = SnackbarKind.Success,
                            actionLabel = UiText.StringResource(R.string.view_cart),
                            isCartAction = true
                        )
                    )
                }
                is Result.Failure -> {
                    _state.update { it.copy(isAddingToCart = false) }
                    if (result.exception is AuthException.UnauthorizedAccess) {
                        _state.update { it.copy(showUnauthorizedDialog = true) }
                    } else {
                        _sideEffects.emit(
                            ProductDetailsSideEffect.ShowSnackbar(
                                message = UiText.Plain(result.exception.message ?: "Failed to add to cart"),
                                kind = SnackbarKind.Error
                            )
                        )
                    }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun dismissUnauthorizedDialog() {
        _state.update { it.copy(showUnauthorizedDialog = false) }
    }

    private fun selectedVariantId(): String? {
        val product = _state.value.product ?: return null
        val color = _state.value.selectedColor
        val size = _state.value.selectedSize

        return product.variants.firstOrNull { variant ->
            val matchesColor = color == null || variant.title.contains(color, ignoreCase = true)
            val matchesSize = size == null || variant.title.contains(size, ignoreCase = true)
            matchesColor && matchesSize
        }?.id ?: product.variants.firstOrNull()?.id
    }
}