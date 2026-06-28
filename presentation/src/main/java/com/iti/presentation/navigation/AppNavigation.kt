package com.iti.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.iti.domain.usecase.IsOnboardingCompletedUseCase
import com.iti.presentation.screens.home.HomeScreen
import com.iti.presentation.screens.onboarding.OnboardingScreen
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.SignInScreen
import com.iti.presentation.screens.auth.SignUpScreen
import com.iti.presentation.screens.splash.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
    val scope = rememberCoroutineScope()
    val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase = koinInject()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen(
                    onAnimationComplete = {
                        scope.launch {
                            val completed = isOnboardingCompletedUseCase().first()
                            backStack.clear()
                            if (completed) {
                                backStack.add(Screen.Home)
                            } else {
                                backStack.add(Screen.OnBoarding)
                            }
                        }
                    }
                )
            }

            entry<Screen.OnBoarding> {
                val onboardingViewModel: OnboardingViewModel = koinViewModel()
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
                )
            }

            entry<Screen.SignIn> {
                SignInScreen(
                    onNavigateToSignUp = { backStack.add(Screen.SignUp) }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
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
