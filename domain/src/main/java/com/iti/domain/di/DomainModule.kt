package com.iti.domain.di

import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetAdsUseCase(get()) }
}
