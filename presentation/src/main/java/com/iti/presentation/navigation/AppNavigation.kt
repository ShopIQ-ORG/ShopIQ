package com.iti.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.iti.presentation.productdetails.ProductDetailsScreen
import com.iti.presentation.screens.home.HomeScreen
import com.iti.presentation.screens.onboarding.OnboardingScreen
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.signin.SignInScreen
import com.iti.presentation.screens.auth.signup.SignUpScreen
import com.iti.presentation.screens.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel
import com.iti.presentation.screens.brands.AllBrandsScreen
import com.iti.presentation.screens.products.AllProductsScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }

    NavDisplay(
        modifier = modifier,
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
                    onNavigateToHome = { backStack.add(Screen.SignIn) }
                )
            }

            entry<Screen.SignIn> {
                SignInScreen(
                    onNavigateToSignUp = { backStack.add(Screen.SignUp) },
                    onNavigateToHome = { backStack.add(Screen.Home) },
                    onNavigateToForgotPassword = { }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToHome = { backStack.add(Screen.Home) },
                    onNavigateToSignIn = { backStack.removeLastOrNull() }
                )
            }

            entry<Screen.Home> {
                HomeScreen(
                    onNavigateToSplash = {
                        backStack.clear()
                        backStack.add(Screen.Splash)
                    },
                    onNavigateToProduct = { productId ->
                        backStack.add(
                            Screen.ProductDetails(
                                productId = productId
                            )
                        )
                    },
                    onNavigateToAllBrands = { backStack.add(Screen.AllBrands) },
                    onNavigateToAllProducts = { brandName ->
                        backStack.add(
                            Screen.AllProducts(
                                brandName
                            )
                        )
                    }
                )
            }

            entry<Screen.AllBrands> {
                AllBrandsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToAllProducts = { brandName ->
                        backStack.add(
                            Screen.AllProducts(
                                brandName
                            )
                        )
                    }
                )
            }

            entry<Screen.AllProducts> { allProductsScreen ->
                AllProductsScreen(
                    brandName = allProductsScreen.brandName,
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            entry<Screen.ProductDetails> { productDetailsScreen ->
                ProductDetailsScreen(
                    productId = productDetailsScreen.productId,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}