package com.iti.domain.di

import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithFacebookUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.domain.usecases.auth.RegisterUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.onboarding.IsOnboardingCompletedUseCase
import com.iti.domain.usecases.onboarding.SetOnboardingCompletedUseCase
import com.iti.domain.usecases.categories.GetCategoriesUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetAdsUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { LoginWithFacebookUseCase(get()) }
    factory { LoginAsGuestUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { com.iti.domain.usecases.products.SearchProductsUseCase(get()) }
    factory { com.iti.domain.usecases.products.GetPopularProductsUseCase(get()) }
    factory { com.iti.domain.usecases.search.GetSearchHistoryUseCase(get()) }
    factory { com.iti.domain.usecases.search.AddSearchQueryUseCase(get()) }
    factory { com.iti.domain.usecases.search.DeleteSearchQueryUseCase(get()) }
    factory { com.iti.domain.usecases.search.ClearSearchHistoryUseCase(get()) }
}
