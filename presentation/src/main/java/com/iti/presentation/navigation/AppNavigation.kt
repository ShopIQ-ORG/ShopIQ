package com.iti.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.iti.presentation.screens.home.HomeScreen
import com.iti.presentation.screens.onboarding.OnBoardingScreen
import com.iti.presentation.screens.auth.SignInScreen
import com.iti.presentation.screens.auth.SignUpScreen
import com.iti.presentation.screens.splash.SplashScreen
import com.iti.presentation.screens.brands.AllBrandsScreen
import com.iti.presentation.screens.products.AllProductsScreen

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen(
                    onNavigateToOnBoarding = { backStack.add(Screen.OnBoarding) }
                )
            }

            entry<Screen.OnBoarding> {
                OnBoardingScreen(
                    onNavigateToSignIn = { backStack.add(Screen.SignIn) }
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
                    },
                    onNavigateToAllBrands = { backStack.add(Screen.AllBrands) },
                    onNavigateToAllProducts = { brandName -> backStack.add(Screen.AllProducts(brandName)) }
                )
            }

            entry<Screen.AllBrands> {
                AllBrandsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToAllProducts = { brandName -> backStack.add(Screen.AllProducts(brandName)) }
                )
            }

            entry<Screen.AllProducts> { allProductsScreen ->
                AllProductsScreen(
                    brandName = allProductsScreen.brandName,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
