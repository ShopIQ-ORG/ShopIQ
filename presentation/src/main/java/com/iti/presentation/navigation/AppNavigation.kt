package com.iti.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.iti.presentation.screens.products.productdetails.ProductDetailsScreen
import com.iti.presentation.screens.home.HomeScreen
import com.iti.presentation.screens.onboarding.OnboardingScreen
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.signin.SignInScreen
import com.iti.presentation.screens.auth.signup.SignUpScreen
import com.iti.presentation.screens.splash.SplashDestination
import com.iti.presentation.screens.splash.SplashScreen
import com.iti.presentation.screens.splash.SplashViewModel
import com.iti.presentation.screens.brands.AllBrandsScreen
import com.iti.presentation.screens.search.SearchScreen
import com.iti.presentation.screens.search.SearchViewModel
import com.iti.presentation.screens.address.AddressScreen
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.screens.cart.CartScreen
import com.iti.presentation.screens.categorydetails.CategoryDetailsScreen
import com.iti.presentation.screens.orderdetails.OrderDetailsScreen
import com.iti.presentation.screens.orders.OrdersScreen
import com.iti.presentation.screens.products.displayallproducts.AllProductsScreen
import com.iti.presentation.screens.products.checkout.PaymentMethodScreen
import com.iti.presentation.screens.products.checkout.CODPaymentScreen
import com.iti.presentation.screens.products.checkout.OnlinePaymentScreen
import com.iti.presentation.screens.products.checkout.PaymentMethodViewModel
import com.iti.presentation.screens.products.checkout.PaymentMethodContract.PaymentMethodType
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun replaceRoot(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = ::navigateBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {

            entry<Screen.Splash> {
                val viewModel: SplashViewModel = koinViewModel()
                val destination by viewModel.destination.collectAsState()
                var isAnimationDone by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    viewModel.checkDestination()
                }

                LaunchedEffect(destination, isAnimationDone) {
                    val dest = destination
                    if (isAnimationDone && dest != null) {
                        when (dest) {
                            is SplashDestination.OnBoarding ->
                                replaceRoot(Screen.OnBoarding)

                            is SplashDestination.SignIn ->
                                replaceRoot(Screen.SignIn)

                            is SplashDestination.Home ->
                                replaceRoot(Screen.Home)
                        }
                    }
                }

                SplashScreen(
                    onAnimationComplete = {
                        isAnimationDone = true
                    }
                )
            }

            entry<Screen.OnBoarding> {
                val onboardingViewModel: OnboardingViewModel = koinViewModel()

                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onNavigateToHome = {
                        navigate(Screen.SignIn)
                    }
                )
            }

            entry<Screen.SignIn> {
                SignInScreen(
                    onNavigateToSignUp = {
                        navigate(Screen.SignUp)
                    },
                    onNavigateToHome = {
                        replaceRoot(Screen.Home)
                    },
                    onNavigateToForgotPassword = { }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToHome = {
                        replaceRoot(Screen.Home)
                    },
                    onNavigateToSignIn = ::navigateBack
                )
            }

            entry<Screen.AiHistory> {
                val viewModel: com.iti.presentation.screens.ai.history.AiHistoryViewModel = org.koin.androidx.compose.koinViewModel()
                com.iti.presentation.screens.ai.history.AiHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::navigateBack
                )
            }

            entry<Screen.Home> {
                HomeScreen(
                    onNavigateToProduct = { productId ->
                        navigate(Screen.ProductDetails(productId))
                    },
                    onNavigateToAllBrands = {
                        navigate(Screen.AllBrands)
                    },
                    onNavigateToAllProducts = { brandName ->
                        navigate(Screen.AllProducts(brandName))
                    },
                    onNavigateToSearch = {
                        navigate(Screen.Search)
                    },
                    onNavigateToAiHistory = {
                        navigate(Screen.AiHistory)
                    },
                    onCategoryClick = { categoryId, categoryTitle ->
                        navigate(Screen.CategoryDetails(categoryId, categoryTitle))
                    },
                    onCartClick = {
                        if (backStack.lastOrNull() !is Screen.Cart) {
                            navigate(Screen.Cart)
                        }
                    },
                    onLogout = {
                        replaceRoot(Screen.SignIn)
                    },
                    onNavigateToOrders = {
                        navigate(Screen.Orders)
                    }
                )
            }

            entry<Screen.AllBrands> {
                AllBrandsScreen(
                    onNavigateBack = ::navigateBack,
                    onNavigateToAllProducts = { brandName ->
                        navigate(Screen.AllProducts(brandName))
                    },
                    onCartClick = {
                        if (backStack.lastOrNull() !is Screen.Cart) {
                            navigate(Screen.Cart)
                        }
                    }
                )
            }

            entry<Screen.AllProducts> { screen ->
                AllProductsScreen(
                    brandName = screen.brandName,
                    onNavigateBack = ::navigateBack,
                    onNavigateToProduct = { productId ->
                        navigate(Screen.ProductDetails(productId))
                    },
                    onNavigateToAuth = {
                        replaceRoot(Screen.SignIn)
                    }
                )
            }

            entry<Screen.CategoryDetails> { screen ->
                CategoryDetailsScreen(
                    categoryId = screen.categoryId,
                    categoryTitle = screen.categoryTitle,
                    onBackClick = ::navigateBack,
                    onNavigateToProduct = { productId ->
                        navigate(Screen.ProductDetails(productId))
                    },
                    onNavigateToSearch = {
                        navigate(Screen.Search)
                    }
                )
            }

            entry<Screen.ProductDetails> { screen ->
                ProductDetailsScreen(
                    productId = screen.productId,
                    onBackClick = ::navigateBack,
                )
            }

            entry<Screen.Cart> {
                CartScreen(
                    onBackClick = ::navigateBack,
                    onCartItemClicked = {
                        navigate(
                            Screen.ProductDetails(it)
                        )
                    },
                    onCheckout = {
                        navigate(Screen.PaymentMethod)
                    },
                    onLogin = {
                        replaceRoot(Screen.SignIn)
                    },
                    onBrowseProducts = ::navigateBack
                )
            }

            entry<Screen.Search> {
                val searchViewModel: SearchViewModel = koinViewModel()

                SearchScreen(
                    viewModel = searchViewModel,
                    onNavigateBack = ::navigateBack,
                    onNavigateToProduct = { productId ->
                        navigate(Screen.ProductDetails(productId))
                    }
                )
            }

            entry<Screen.PaymentMethod> {
                val paymentViewModel: PaymentMethodViewModel = koinViewModel()

                PaymentMethodScreen(
                    viewModel = paymentViewModel,
                    onNavigateBack = ::navigateBack,
                    onNavigateToNextStep = { methodType ->
                        when (methodType) {
                            PaymentMethodType.COD -> {
                                navigate(Screen.CODPayment)
                            }

                            PaymentMethodType.ONLINE -> {
                                navigate(Screen.OnlinePayment)
                            }
                        }
                    }
                )
            }

            entry<Screen.CODPayment> {
                CODPaymentScreen(onNavigateBack = ::navigateBack)
            }

            entry<Screen.OnlinePayment> {
                OnlinePaymentScreen(onNavigateBack = ::navigateBack)
            }

            entry<Screen.Orders> {
                OrdersScreen(
                    onNavigateBack = ::navigateBack,
                    onOrderClick = {
                        navigate(Screen.OrderDetails(it))
                    },
                )
            }

            entry<Screen.OrderDetails> {
                OrderDetailsScreen(
                    order = it.order,
                    onNavigateBack = ::navigateBack,
                    onNavigateToSupport = {}
                )
            }

            entry<Screen.ManageAddresses> {
                val addressViewModel: AddressViewModel = koinViewModel()

                AddressScreen(
                    viewModel = addressViewModel,
                    onNavigateBack = ::navigateBack
                )
            }
        }
    )
}