package com.iti.presentation.di

import com.iti.presentation.productdetails.ProductDetailsViewModel
import org.koin.core.module.dsl.viewModel
import com.iti.presentation.main.MainViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
}
