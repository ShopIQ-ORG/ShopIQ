package com.iti.presentation.screens.onboarding

import com.iti.presentation.util.UiText

class OnboardingContract {

    data class State(
        val currentPage: Int = 0,
        val isLoading: Boolean = false
    )

    sealed interface Intent {
        object Next : Intent
        object Previous : Intent
        object Skip : Intent
        data class PageChanged(val page: Int) : Intent
    }

    sealed interface Effect {
        object NavigateToHome : Effect
        object NavigateToSignIn : Effect
        data class ShowError(val message: UiText) : Effect
    }
}