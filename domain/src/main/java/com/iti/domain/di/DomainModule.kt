package com.iti.domain.di

import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithFacebookUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.domain.usecases.auth.RegisterUseCase
import com.iti.domain.usecase.IsOnboardingCompletedUseCase
import com.iti.domain.usecase.SetOnboardingCompletedUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { LoginWithFacebookUseCase(get()) }
    factory { LoginAsGuestUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
}
