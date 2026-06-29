package com.iti.presentation.di

import com.iti.presentation.screens.home.HomeViewModel
import com.iti.presentation.main.MainViewModel
import com.iti.presentation.productdetails.ProductDetailsViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import com.iti.presentation.screens.auth.signin.SignInViewModel
import com.iti.presentation.screens.auth.signup.SignUpViewModel
import com.iti.presentation.screens.brands.AllBrandsViewModel
import com.iti.presentation.screens.category.CategoryViewModel
import com.iti.presentation.screens.products.AllProductsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { CategoryViewModel() }
    viewModel { AllBrandsViewModel(get()) }
    viewModel { AllProductsViewModel(get()) }
    viewModel { ProductDetailsViewModel(get()) }

}