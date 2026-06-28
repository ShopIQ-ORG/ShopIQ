package com.iti.presentation.navigation

sealed class Screen {
    data object Splash : Screen()
    data object OnBoarding : Screen()
    data object SignIn : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
}
