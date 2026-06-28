package com.iti.presentation.di

import com.iti.presentation.main.MainViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
}
