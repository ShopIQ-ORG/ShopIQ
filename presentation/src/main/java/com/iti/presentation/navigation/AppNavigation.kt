//
//  AppNavigation.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

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
import com.iti.presentation.screens.auth.emailverification.EmailVerificationScreen
import com.iti.presentation.screens.auth.forgotpassword.ForgotPasswordScreen
import com.iti.presentation.screens.cart.CartScreen
import com.iti.presentation.screens.categorydetails.CategoryDetailsScreen
import com.iti.presentation.screens.orderdetails.OrderDetailsScreen
import com.iti.presentation.screens.orders.OrdersScreen
import com.iti.presentation.screens.products.displayallproducts.AllProductsScreen
import com.iti.presentation.screens.checkout.PaymentMethodViewModel
import com.iti.presentation.screens.payment.PaymentScreen
import com.iti.presentation.screens.payment.PaymentViewModel
import com.iti.presentation.screens.profile.AccountSettingsScreen
import com.iti.presentation.screens.profile.EditProfileScreen
import com.iti.presentation.screens.profile.LocalizationCurrencyScreen
import com.iti.presentation.screens.profile.AddressManagementScreen
import com.iti.presentation.screens.profile.AddEditAddressScreen
import com.iti.presentation.screens.address.components.AddressMapPicker
import com.iti.presentation.screens.checkout.PaymentMethodContract
import com.iti.presentation.screens.checkout.PaymentMethodScreen
import com.iti.presentation.screens.checkout.CheckoutScreen
import com.iti.presentation.screens.checkout.CheckoutViewModel
import com.iti.presentation.screens.profile.ProfileViewModel
import com.iti.presentation.screens.checkout.summary.CheckoutSummaryScreen
import com.iti.presentation.screens.checkout.summary.OrderSuccessScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
    val profileViewModel: ProfileViewModel = koinViewModel()

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

                            is SplashDestination.EmailVerification ->
                                replaceRoot(Screen.EmailVerification(dest.email))
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
                        replaceRoot(Screen.Home)
                    },
                    onNavigateToSignIn = {
                        replaceRoot(Screen.SignIn)
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
                    onNavigateToForgotPassword = {
                        navigate(Screen.ForgotPassword)
                    },
                    onNavigateToEmailVerification = {
                        navigate(Screen.EmailVerification(it))
                    }
                )
            }

            entry<Screen.SignUp> {
                SignUpScreen(
                    onNavigateToHome = {
                        replaceRoot(Screen.Home)
                    },
                    onNavigateToEmailVerification = {
                        navigate(Screen.EmailVerification(it))
                    },
                    onNavigateToSignIn = ::navigateBack
                )
            }

            entry<Screen.AiHistory> {
                val viewModel: com.iti.presentation.screens.ai.history.AiHistoryViewModel =
                    org.koin.androidx.compose.koinViewModel()
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
                    },
                    onNavigateToEditProfile = {
                        navigate(Screen.EditProfile)
                    },
                    onNavigateToLocalizationCurrency = {
                        navigate(Screen.LocalizationCurrency)
                    },
                    onNavigateToAddressManagement = {
                        navigate(Screen.AddressManagement)
                    },
                    profileViewModel = profileViewModel
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
                    },
                    onNavigateToAuth = {
                        replaceRoot(Screen.SignIn)
                    }
                )
            }

            entry<Screen.ProductDetails> { screen ->
                ProductDetailsScreen(
                    productId = screen.productId,
                    onBackClick = ::navigateBack,
                    onLogin = {
                        replaceRoot(Screen.SignIn)
                    }
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
                        navigate(Screen.Checkout)
                    },
                    onLogin = {
                        replaceRoot(Screen.SignIn)
                    },
                    onBrowseProducts = ::navigateBack
                )
            }

            entry<Screen.Checkout> {
                val checkoutViewModel: CheckoutViewModel = koinViewModel()
                val addressViewModel: AddressViewModel = koinViewModel()

                CheckoutScreen(
                    viewModel = checkoutViewModel,
                    addressViewModel = addressViewModel,
                    onNavigateBack = ::navigateBack,
                    onNavigateToHome = {
                        replaceRoot(Screen.Home)
                    }
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
                    onNavigateToNextStep = { methodType: PaymentMethodContract.PaymentMethodType, amountCents: Long ->
                        when (methodType) {
                            PaymentMethodContract.PaymentMethodType.COD -> {
                                navigate(Screen.CODPayment)
                            }

                            PaymentMethodContract.PaymentMethodType.ONLINE -> {
                                navigate(Screen.OnlinePayment(amountCents))
                            }
                        }
                    }
                )
            }

            entry<Screen.CODPayment> {
                CheckoutSummaryScreen(
                    paymentMethod = PaymentMethodContract.PaymentMethodType.COD,
                    onNavigateBack = ::navigateBack,
                    onNavigateToOrderConfirmation = {
                        navigate(Screen.OrderSuccess)
                    }
                )
            }

            entry<Screen.OnlinePayment> { screen ->
                val paymentViewModel: PaymentViewModel = koinViewModel()

                PaymentScreen(
                    viewModel = paymentViewModel,
                    amountCents = screen.amountCents,
                    integrationId = com.iti.data.BuildConfig.PAYMOB_INTEGRATION_ID.toIntOrNull() ?: 5276242,
                    onPaymentSuccess = {
                        navigate(Screen.OnlinePaymentSummary)
                    },
                    onNavigateBack = ::navigateBack
                )
            }

            entry<Screen.OnlinePaymentSummary> {
                CheckoutSummaryScreen(
                    paymentMethod = PaymentMethodContract.PaymentMethodType.ONLINE,
                    onNavigateBack = ::navigateBack,
                    onNavigateToOrderConfirmation = {
                        navigate(Screen.OrderSuccess)
                    }
                )
            }

            entry<Screen.OrderSuccess> {
                OrderSuccessScreen(
                    onNavigateToHome = {
                        replaceRoot(Screen.Home)
                    },
                    onNavigateToOrders = {
                        replaceRoot(Screen.Home)
                        navigate(Screen.Orders)
                    }
                )
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

            entry<Screen.AccountSettings> {
                val viewModel = profileViewModel
                AccountSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::navigateBack,
                    onLogout = { replaceRoot(Screen.SignIn) },
                    onNavigateToEditProfile = { navigate(Screen.EditProfile) },
                    onNavigateToLocalizationCurrency = { navigate(Screen.LocalizationCurrency) },
                    onNavigateToAddressManagement = { navigate(Screen.AddressManagement) },
                    onNavigateToOrders = { navigate(Screen.Orders) }
                )
            }

            entry<Screen.EditProfile> {
                val viewModel = profileViewModel
                EditProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::navigateBack
                )
            }

            entry<Screen.LocalizationCurrency> {
                val viewModel = profileViewModel
                LocalizationCurrencyScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::navigateBack
                )
            }

            entry<Screen.AddressManagement> {
                val viewModel = profileViewModel
                AddressManagementScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::navigateBack,
                    onNavigateToAddAddress = { navigate(Screen.AddressValidation(30.0444, 31.2357)) },
                    onNavigateToEditAddress = { id -> navigate(Screen.AddEditAddress(id)) }
                )
            }

            entry<Screen.AddEditAddress> { screen ->
                val viewModel = profileViewModel
                AddEditAddressScreen(
                    viewModel = viewModel,
                    addressId = screen.addressId,
                    onNavigateBack = ::navigateBack,
                    onNavigateToValidation = { lat, lng, street, city, country, postalCode, label, isDefault, recipientName, phone, id ->
                        navigate(
                            Screen.AddressValidation(
                                latitude = lat,
                                longitude = lng,
                                street = street,
                                city = city,
                                country = country,
                                postalCode = postalCode,
                                label = label,
                                isDefault = isDefault,
                                recipientName = recipientName,
                                phone = phone,
                                addressId = id
                            )
                        )
                    }
                )
            }

            entry<Screen.AddressValidation> { screen ->
                val addressViewModel: AddressViewModel = koinViewModel()
                AddressMapPicker(
                    initialLatitude = screen.latitude,
                    initialLongitude = screen.longitude,
                    onLocationConfirmed = { lat, lng ->
                        profileViewModel.updateTempAddressLocation(lat, lng)
                        val hasAddEditAddress = backStack.any { it is Screen.AddEditAddress }
                        if (hasAddEditAddress) {
                            navigateBack()
                        } else {
                            if (backStack.isNotEmpty()) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            navigate(Screen.AddEditAddress(null))
                        }
                    },
                    onBackClick = ::navigateBack,
                    viewModel = addressViewModel
                )
            }

            entry<Screen.ForgotPassword> {
                ForgotPasswordScreen(onNavigateBack = ::navigateBack)
            }

            entry<Screen.EmailVerification> { screen ->
                EmailVerificationScreen(
                    email = screen.email,
                    onNavigateToSignIn = { replaceRoot(Screen.SignIn) },
                    onNavigateToHome = { replaceRoot(Screen.Home) }
                )
            }
        }
    )
}