package com.iti.domain.di

import com.iti.domain.usecase.IsOnboardingCompletedUseCase
import com.iti.domain.usecase.SetOnboardingCompletedUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
}
