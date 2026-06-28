package com.iti.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.usecase.SetOnboardingCompletedUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingContract.State())
    val state: StateFlow<OnboardingContract.State> = _state.asStateFlow()

    private val _effect = Channel<OnboardingContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun sendIntent(intent: OnboardingContract.Intent) {
        when (intent) {
            is OnboardingContract.Intent.Next -> handleNext()
            is OnboardingContract.Intent.Previous -> handlePrevious()
            is OnboardingContract.Intent.Skip -> handleComplete()
            is OnboardingContract.Intent.PageChanged -> handlePageChanged(intent.page)
        }
    }

    private fun handleNext() {
        val currentPage = _state.value.currentPage
        if (currentPage < 2) {
            _state.value = _state.value.copy(currentPage = currentPage + 1)
        } else {
            handleComplete()
        }
    }

    private fun handlePrevious() {
        val currentPage = _state.value.currentPage
        if (currentPage > 0) {
            _state.value = _state.value.copy(currentPage = currentPage - 1)
        }
    }

    private fun handlePageChanged(page: Int) {
        if (_state.value.currentPage != page) {
            _state.value = _state.value.copy(currentPage = page)
        }
    }

    private fun handleComplete() {
        viewModelScope.launch {
            setOnboardingCompletedUseCase()
            _effect.send(OnboardingContract.Effect.NavigateToHome)
        }
    }
}
