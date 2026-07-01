package com.iti.presentation.screens.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        }
    }

    private fun loadBrands() {
        _state.update { it.copy(screenState = AllBrandsContract.ScreenState.Loading) }
        viewModelScope.launch {
            getBrandsUseCase().collect { result ->
                _state.update {
                    it.copy(
                        screenState = when (result) {
                            is Result.Loading -> AllBrandsContract.ScreenState.Loading
                            is Result.Success -> AllBrandsContract.ScreenState.Success(result.data)
                            is Result.Failure -> AllBrandsContract.ScreenState.Failure(
                                result.exception.message
                                    ?.let { msg -> UiText.Plain(msg) }
                                    ?: UiText.StringResource(R.string.error_loading_brands)
                            )
                        }
                    )
                }
            }
        }
    }

    private fun emitEffect(effect: AllBrandsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}