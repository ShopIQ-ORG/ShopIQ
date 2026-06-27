package com.iti.presentation.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Used as keys for the Navigation3 back stack.
 */
sealed class Screen {
    data object Splash : Screen()
    data object OnBoarding : Screen()
    data object SignIn : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
}
