package com.iti.presentation.di

import com.iti.presentation.screens.home.HomeViewModel
import com.iti.presentation.productdetails.ProductDetailsViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.signin.SignInViewModel
import com.iti.presentation.screens.auth.signup.SignUpViewModel
import com.iti.presentation.screens.brands.AllBrandsViewModel
import com.iti.presentation.screens.category.CategoryViewModel
import com.iti.presentation.screens.products.AllProductsViewModel
import com.iti.presentation.screens.search.SearchViewModel
import com.iti.presentation.screens.splash.SplashViewModel
import com.iti.presentation.util.NetworkMonitor
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    single { NetworkMonitor(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { AllBrandsViewModel(get()) }
    viewModel { AllProductsViewModel(get()) }
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
}