package com.iti.presentation.main

class MainContract {

    sealed interface State {
        object Loading : State
        object ShowOnboarding : State
        object ShowHome : State
    }

    sealed interface Intent {
        object CheckOnboarding : Intent
    }
}
