package com.iti.domain.di

import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecase.IsOnboardingCompletedUseCase
import com.iti.domain.usecase.SetOnboardingCompletedUseCase
import com.iti.domain.usecases.categories.GetCategoriesUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
}
