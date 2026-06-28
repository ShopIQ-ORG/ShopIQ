package com.iti.presentation.onboarding

class OnboardingContract {

    data class State(
        val currentPage: Int = 0
    )

    sealed interface Intent {
        object Next : Intent
        object Previous : Intent
        object Skip : Intent
        data class PageChanged(val page: Int) : Intent
    }

    sealed interface Effect {
        object NavigateToHome : Effect
    }
}
