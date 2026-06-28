package com.iti.presentation.di

import com.iti.presentation.main.MainViewModel
import com.iti.presentation.screens.onboarding.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import com.iti.presentation.screens.auth.signin.SignInViewModel
import com.iti.presentation.screens.auth.signup.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import com.iti.presentation.screens.category.CategoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { CategoryViewModel() }
}