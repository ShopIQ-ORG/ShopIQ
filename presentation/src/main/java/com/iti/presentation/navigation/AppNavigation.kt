package com.iti.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.iti.presentation.screens.home.HomeScreen
import com.iti.presentation.screens.onboarding.OnboardingScreen
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.SignInScreen
import com.iti.presentation.screens.auth.SignUpScreen
import com.iti.presentation.screens.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen(
                    onAnimationComplete = { backStack.add(Screen.OnBoarding) }
                )
            }

            entry<Screen.OnBoarding> {
                val onboardingViewModel: OnboardingViewModel = koinViewModel()
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onNavigateToHome = { backStack.add(Screen.Home) }
                )
            }

            entry<Screen.SignIn> {
                SignInScreen(
                    onNavigateToSignUp = { backStack.add(Screen.SignUp) }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToHome = { backStack.add(Screen.Home) }
                )
            }

            entry<Screen.Home> {
                HomeScreen(
                    onNavigateToSplash = {
                        backStack.clear()
                        backStack.add(Screen.Splash)
                    }
                )
            }
        }
    )
}
