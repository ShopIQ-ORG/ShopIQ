package com.iti.domain.di

import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.products.GetProductsPaginatedUseCase
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithFacebookUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.domain.usecases.auth.RegisterUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.cart.AddCartItemUseCase
import com.iti.domain.usecases.cart.ApplyDiscountCodesUseCase
import com.iti.domain.usecases.cart.GetCartUseCase
import com.iti.domain.usecases.cart.ObserveCartItemCountUseCase
import com.iti.domain.usecases.cart.RemoveCartItemUseCase
import com.iti.domain.usecases.cart.UpdateCartItemQuantityUseCase
import com.iti.domain.usecases.onboarding.IsOnboardingCompletedUseCase
import com.iti.domain.usecases.onboarding.SetOnboardingCompletedUseCase
import com.iti.domain.usecases.categories.GetCategoriesUseCase
import com.iti.domain.usecases.location.GetCurrentLocationUseCase
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.address.SaveAddressUseCase
import com.iti.domain.usecases.address.DeleteAddressUseCase
import com.iti.domain.usecases.auth.ReloadAndGetCurrentUserUseCase
import com.iti.domain.usecases.auth.SendEmailVerificationUseCase
import com.iti.domain.usecases.auth.SendPasswordResetEmailUseCase
import com.iti.domain.usecases.categories.GetProductsByCategoryUseCase
import com.iti.domain.usecases.orders.GetOrdersUseCase
import org.koin.core.module.dsl.factoryOf
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.IsProductFavoriteUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.usecases.shopify.GetValidShopifyTokenUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetProductsPaginatedUseCase(get()) }
    factory { GetBrandsUseCase(get()) }
    factory { GetAdsUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { LoginWithFacebookUseCase(get()) }
    factory { LoginAsGuestUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { GetProductsByNumberUseCase(get()) }
    factory { GetProductDetailsUseCase(get()) }
    factory { IsOnboardingCompletedUseCase(get()) }
    factory { SetOnboardingCompletedUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetProductsByCategoryUseCase(get()) }
    factory { AddProductToFavoritesUseCase(get()) }
    factory { RemoveProductFromFavoritesUseCase(get()) }
    factory { GetFavoriteProductsUseCase(get()) }
    factory { IsProductFavoriteUseCase(get()) }
    factory { com.iti.domain.usecases.products.SearchProductsUseCase(get()) }
    factory { com.iti.domain.usecases.products.GetPopularProductsUseCase(get()) }
    factory { com.iti.domain.usecases.search.GetSearchHistoryUseCase(get()) }
    factory { com.iti.domain.usecases.search.AddSearchQueryUseCase(get()) }
    factory { com.iti.domain.usecases.search.DeleteSearchQueryUseCase(get()) }
    factory { com.iti.domain.usecases.search.ClearSearchHistoryUseCase(get()) }
    factory { GetCartUseCase(get(), get()) }
    factory { UpdateCartItemQuantityUseCase(get(), get()) }
    factory { RemoveCartItemUseCase(get(), get()) }
    factory { ApplyDiscountCodesUseCase(get(), get()) }
    factory { AddCartItemUseCase(get(), get()) }
    factory { GetCurrentLocationUseCase(get()) }
    factory { GetSavedAddressesUseCase(get()) }
    factory { SaveAddressUseCase(get()) }
    factory { DeleteAddressUseCase(get()) }
    factory { com.iti.domain.usecases.address.GetPlaceSuggestionsUseCase(get()) }
    factory { com.iti.domain.usecases.address.SearchLocationByNameUseCase(get()) }
    factoryOf(::ObserveCartItemCountUseCase)
    factoryOf(::GetOrdersUseCase)
    factoryOf(::SendPasswordResetEmailUseCase)
    factoryOf(::SendEmailVerificationUseCase)
    factoryOf(::GetValidShopifyTokenUseCase)
    factoryOf(::ReloadAndGetCurrentUserUseCase)

    // Currency Use Cases
    factory { com.iti.domain.usecases.currency.GetSelectedCurrencyUseCase(get()) }
    factory { com.iti.domain.usecases.currency.GetPopularCurrenciesUseCase(get()) }
    factory { com.iti.domain.usecases.currency.GetExchangeRateHistoryUseCase(get()) }
    factory { com.iti.domain.usecases.currency.FetchExchangeRatesUseCase(get()) }
    factory { com.iti.domain.usecases.currency.SelectCurrencyUseCase(get()) }
}
