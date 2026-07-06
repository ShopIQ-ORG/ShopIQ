package com.iti.presentation.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WishlistUiState>(WishlistUiState.Loading)
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<WishlistUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        handleIntent(WishlistIntent.LoadFavorites)
    }

    fun handleIntent(intent: WishlistIntent) {
        when (intent) {
            is WishlistIntent.LoadFavorites -> loadFavorites()
            is WishlistIntent.RemoveFromFavorites -> removeFromFavorites(intent.productId)
        }
    }

    private fun loadFavorites() {
        if (authRepository.isGuest()) {
            _uiState.value = WishlistUiState.RequireAuth
            return
        }

        viewModelScope.launch {
            getFavoriteProductsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = WishlistUiState.Loading
                    is Result.Success -> _uiState.value = WishlistUiState.Success(result.data)
                    is Result.Failure -> _uiState.value = WishlistUiState.Error(result.exception.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            try {
                removeProductFromFavoritesUseCase(productId)
                _uiEffect.send(WishlistUiEffect.ShowSnackbar(UiText.StringResource(com.iti.presentation.R.string.removed_from_wishlist)))
            } catch (e: Exception) {
                _uiEffect.send(WishlistUiEffect.ShowSnackbar(UiText.Plain(e.message ?: "Failed to remove product")))
            }
        }
    }
}
