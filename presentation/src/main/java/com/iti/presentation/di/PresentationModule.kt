package com.iti.presentation.di

import com.iti.presentation.screens.home.viewmodel.HomeViewModel
import com.iti.presentation.screens.products.productdetails.ProductDetailsViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.signin.SignInViewModel
import com.iti.presentation.screens.auth.signup.SignUpViewModel
import com.iti.presentation.screens.brands.AllBrandsViewModel
import com.iti.presentation.screens.cart.CartViewModel
import com.iti.presentation.screens.category.CategoryViewModel
import com.iti.presentation.screens.categorydetails.CategoryDetailsViewModel
import com.iti.presentation.screens.home.viewmodel.CartBadgeViewModel
import com.iti.presentation.screens.orderdetails.OrderDetailsViewModel
import com.iti.presentation.screens.orders.OrdersViewModel
import com.iti.presentation.screens.products.checkout.PaymentMethodViewModel
import com.iti.presentation.screens.search.SearchViewModel
import com.iti.presentation.screens.products.displayallproducts.AllProductsViewModel
import com.iti.presentation.screens.products.displayallproducts.AllProductsFilterManager
import com.iti.presentation.screens.splash.SplashViewModel
import com.iti.presentation.screens.wishlist.WishlistViewModel
import com.iti.domain.usecases.ai.GetChatHistoryUseCase
import com.iti.domain.usecases.ai.SendChatMessageUseCase
import com.iti.presentation.screens.ai.AiChatViewModel
import com.iti.presentation.screens.ai.history.AiHistoryViewModel
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.screens.profile.ProfileViewModel
import com.iti.presentation.util.NetworkMonitor
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    single { NetworkMonitor(get()) }
    single { GetChatHistoryUseCase(get()) }
    single { SendChatMessageUseCase(get()) }
    single { com.iti.domain.usecases.ai.ClearChatHistoryUseCase(get()) }
    viewModel { AiChatViewModel(get(), get(), get()) }
    viewModel { AiHistoryViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { CategoryDetailsViewModel(get(), get(), get(), get(), get()) }
    viewModel { AllBrandsViewModel(get()) }
    factory { AllProductsFilterManager() }
    viewModel { AllProductsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProductDetailsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CartViewModel(get(), get(), get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
    viewModelOf(::CartBadgeViewModel)
    viewModel { WishlistViewModel(get(), get(), get(), get()) }
    viewModel { PaymentMethodViewModel() }
    viewModelOf(::OrdersViewModel)
    viewModelOf(::OrderDetailsViewModel)

    viewModel { AddressViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}