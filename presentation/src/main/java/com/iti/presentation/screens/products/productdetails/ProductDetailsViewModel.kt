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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import com.iti.presentation.util.UiText
import com.iti.presentation.R
import com.iti.domain.usecases.products.AddProductReviewUseCase
import com.iti.domain.usecases.products.UpdateProductReviewUseCase
import com.iti.domain.usecases.products.DeleteProductReviewUseCase
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val addToCartUseCase: AddCartItemUseCase,
    private val addProductReviewUseCase: AddProductReviewUseCase,
    private val updateProductReviewUseCase: UpdateProductReviewUseCase,
    private val deleteProductReviewUseCase: DeleteProductReviewUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsUiState())
    val state: StateFlow<ProductDetailsUiState> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProductDetailsSideEffect>()
    val sideEffects: SharedFlow<ProductDetailsSideEffect> = _sideEffects.asSharedFlow()

    private val favoriteOverride = MutableStateFlow<Boolean?>(null)
    private var loadProductDetailsJob: kotlinx.coroutines.Job? = null

    fun handleIntent(intent: ProductDetailsIntent) {
        when (intent) {
            is ProductDetailsIntent.LoadProductDetails -> loadProductDetails(intent.productId)
            is ProductDetailsIntent.SelectColor -> selectColor(intent.color)
            is ProductDetailsIntent.SelectSize -> selectSize(intent.size)
            is ProductDetailsIntent.SelectImage -> selectImage(intent.index)
            is ProductDetailsIntent.ToggleWishlist -> toggleWishlist()
            is ProductDetailsIntent.AddToCart -> addToCart()
            is ProductDetailsIntent.DismissUnauthorizedDialog -> dismissUnauthorizedDialog()
            is ProductDetailsIntent.SubmitReview -> submitReview(intent.rating, intent.title, intent.body)
            is ProductDetailsIntent.EditReview -> editReview(intent.reviewId, intent.rating, intent.title, intent.body)
            is ProductDetailsIntent.DeleteReview -> deleteReview(intent.reviewId)
        }
    }

    private fun loadProductDetails(productId: Long) {
        viewModelScope.launch {
            val userRes = getCurrentUserUseCase()
            if (userRes is Result.Success && userRes.data is User.AuthenticatedUser) {
                val user = userRes.data as User.AuthenticatedUser
                _state.update { it.copy(currentUserName = user.fullName) }
                observeFavoriteStatus(productId.toString())
            }
        }
        loadProductDetailsJob?.cancel()
        loadProductDetailsJob = viewModelScope.launch {
            getProductDetailsUseCase(productId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        if (_state.value.product == null) {
                            _state.update { it.copy(isLoading = true, product = null, error = null) }
                        }
                    }
                    is Result.Success -> {
                        val product = result.data
                        
                        _state.update {
                            it.copy(
                                isLoading = false,
                                product = product,
                                // Default selection logic
                                selectedColor = "Beige", // default as per mockup
                                selectedSize = "M",      // default as per mockup
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
        // Extract the numeric part only (e.g. "gid://shopify/Product/123" -> "123")
        val cleanProductId = productId.substringAfterLast("/")

        viewModelScope.launch {
            combine(
                getFavoriteProductsUseCase(),
                favoriteOverride
            ) { result: Result<List<com.iti.domain.models.Product>>, override: Boolean? ->
                if (result is Result.Success) {
                    // Compare by clean numeric ID so Room & GID IDs both match
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
                    // Optimistic update via override - happens IMMEDIATELY
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
                    _sideEffects.emit(ProductDetailsSideEffect.ShowToast(message))
                    
                    // Keep the override for a bit
                    kotlinx.coroutines.delay(1000)
                    favoriteOverride.value = null
                } catch (e: Exception) {
                    // Revert optimistic update
                    favoriteOverride.value = null
                    _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain("Error: ${e.message}")))
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
                _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain("Please select options first")))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isAddingToCart = true) }

            when (val result = addToCartUseCase(variantId = variantId, quantity = 1)) {
                is Result.Success -> {
                    _state.update { it.copy(isAddingToCart = false) }
                    _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain("Added to Cart!")))
                }
                is Result.Failure -> {
                    _state.update { it.copy(isAddingToCart = false) }
                    if (result.exception is AuthException.UnauthorizedAccess) {
                        _state.update { it.copy(showUnauthorizedDialog = true) }
                    } else {
                        _sideEffects.emit(
                            ProductDetailsSideEffect.ShowToast(
                                UiText.Plain(result.exception.message ?: "Failed to add to cart")
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

    private fun submitReview(rating: Int, title: String, body: String) {
        val product = _state.value.product ?: return
        viewModelScope.launch {
            val userRes = getCurrentUserUseCase()
            if (userRes !is Result.Success || userRes.data !is User.AuthenticatedUser) {
                _state.update { it.copy(showUnauthorizedDialog = true) }
                return@launch
            }
            
            val user = userRes.data as User.AuthenticatedUser
            _state.update { it.copy(isSubmittingReview = true, reviewError = null) }

            addProductReviewUseCase(
                productId = product.id,
                customerName = user.fullName,
                rating = rating,
                title = title,
                body = body,
                avatarUrl = user.avatarUrl
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isSubmittingReview = true) }
                    }
                    is Result.Success -> {
                        _state.update { currentState ->
                            val currentProduct = currentState.product
                            val updatedProduct = if (currentProduct != null) {
                                val newReview = com.iti.domain.models.ProductReview(
                                    id = "temp_${System.currentTimeMillis()}",
                                    customerName = user.fullName,
                                    rating = rating,
                                    title = title,
                                    body = body,
                                    createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                                    approved = true,
                                    avatarUrl = user.avatarUrl
                                )
                                currentProduct.copy(reviews = currentProduct.reviews + newReview)
                            } else null

                            currentState.copy(
                                isSubmittingReview = false,
                                reviewError = null,
                                product = updatedProduct
                            )
                        }
                        _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.StringResource(R.string.review_submitted_successfully)))
                        // Re-load product details to fetch new reviews in the background
                        val numericId = product.id.substringAfterLast("/").toLongOrNull()
                        if (numericId != null) {
                            loadProductDetails(numericId)
                        }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isSubmittingReview = false, reviewError = result.exception.message) }
                        _sideEffects.emit(
                            ProductDetailsSideEffect.ShowToast(
                                UiText.Plain(result.exception.message ?: "Failed to submit review")
                            )
                        )
                    }
                }
            }
        }
    }

    private fun editReview(reviewId: String, rating: Int, title: String, body: String) {
        val product = _state.value.product ?: return
        viewModelScope.launch {
            val userRes = getCurrentUserUseCase()
            if (userRes !is Result.Success || userRes.data !is User.AuthenticatedUser) {
                _state.update { it.copy(showUnauthorizedDialog = true) }
                return@launch
            }

            val user = userRes.data as User.AuthenticatedUser
            _state.update { it.copy(isSubmittingReview = true, reviewError = null) }

            updateProductReviewUseCase(
                reviewId = reviewId,
                customerName = user.fullName,
                rating = rating,
                title = title,
                body = body,
                avatarUrl = user.avatarUrl
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isSubmittingReview = true) }
                    }
                    is Result.Success -> {
                        _state.update { currentState ->
                            val currentProduct = currentState.product
                            val updatedProduct = if (currentProduct != null) {
                                val updatedList = currentProduct.reviews.map { review ->
                                    if (review.id == reviewId) {
                                        review.copy(
                                            rating = rating,
                                            title = title,
                                            body = body,
                                            createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date())
                                        )
                                    } else review
                                }
                                currentProduct.copy(reviews = updatedList)
                            } else null

                            currentState.copy(
                                isSubmittingReview = false,
                                reviewError = null,
                                product = updatedProduct
                            )
                        }
                        _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain("Review updated successfully!")))
                        val numericId = product.id.substringAfterLast("/").toLongOrNull()
                        if (numericId != null) {
                            loadProductDetails(numericId)
                        }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isSubmittingReview = false, reviewError = result.exception.message) }
                        _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain(result.exception.message ?: "Failed to update review")))
                    }
                }
            }
        }
    }

    private fun deleteReview(reviewId: String) {
        val product = _state.value.product ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true) }

            deleteProductReviewUseCase(
                productId = product.id,
                reviewId = reviewId
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isSubmittingReview = true) }
                    }
                    is Result.Success -> {
                        _state.update { currentState ->
                            val currentProduct = currentState.product
                            val updatedProduct = if (currentProduct != null) {
                                val filteredList = currentProduct.reviews.filter { it.id != reviewId }
                                currentProduct.copy(reviews = filteredList)
                            } else null

                            currentState.copy(
                                isSubmittingReview = false,
                                product = updatedProduct
                            )
                        }
                        _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain("Review deleted!")))
                        val numericId = product.id.substringAfterLast("/").toLongOrNull()
                        if (numericId != null) {
                            loadProductDetails(numericId)
                        }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isSubmittingReview = false) }
                        _sideEffects.emit(ProductDetailsSideEffect.ShowToast(UiText.Plain(result.exception.message ?: "Failed to delete review")))
                    }
                }
            }
        }
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