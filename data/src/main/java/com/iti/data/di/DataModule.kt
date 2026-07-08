package com.iti.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.iti.data.repositories.AddressRepositoryImpl
import com.iti.data.repositories.AuthRepositoryImpl
import com.iti.data.repositories.CartRepositoryImpl
import com.iti.data.repositories.ChatbotRepositoryImpl
import com.iti.data.repositories.CurrencyRepositoryImpl
import com.iti.data.repositories.LocationTrackerImpl
import com.iti.data.repositories.OnboardingRepositoryImpl
import com.iti.data.repositories.OrdersRepositoryImpl
import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.repositories.SearchHistoryRepositoryImpl
import com.iti.data.sources.local.currency.CurrencyLocalDataSource
import com.iti.data.sources.local.currency.CurrencyLocalDataSourceImpl
import com.iti.data.sources.local.room.AppDatabase
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSourceImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSourceImpl
import com.iti.data.sources.remote.orders.OrdersRemoteDataSource
import com.iti.data.sources.remote.orders.OrdersRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartIdDataSource
import com.iti.data.sources.remote.cart.CartIdRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.data.sources.remote.cart.CartRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartResponseValidator
import com.iti.data.sources.remote.currency.CurrencyRemoteDataSource
import com.iti.data.sources.remote.currency.CurrencyRemoteDataSourceImpl
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSource
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSourceImpl
import com.iti.data.sources.remote.user.UserRemoteDataSource
import com.iti.data.sources.remote.user.UserRemoteDataSourceImpl
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.address.AddressRepository
import com.iti.domain.repositories.ai.ChatbotRepository
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.currency.CurrencyRepository
import com.iti.domain.repositories.location.LocationTracker
import com.iti.domain.repositories.onboarding.OnboardingRepository
import com.iti.domain.repositories.orders.OrdersRepository
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.repositories.search.SearchHistoryRepository
import com.iti.domain.util.CacheInvalidator
import com.iti.domain.util.ShopifyTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import com.iti.data.sources.remote.checkout.CheckoutRemoteDataSource
import com.iti.data.sources.remote.checkout.CheckoutRemoteDataSourceImpl
import com.iti.data.repositories.CheckoutRepositoryImpl
import com.iti.domain.repositories.checkout.CheckoutRepository

val dataModule = module {
    single { HttpClient(OkHttp) }
    single { Gson() }
    single(named("adminApolloClient")) { ShopifyNetworkConfig.apolloClient }
    single(named("storefrontApolloClient")) { ShopifyNetworkConfig.storefrontApolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get(named("adminApolloClient"))) }
    single<ProductsRepository> { ProductsRepositoryImpl(get(), get(), get(), get(), get()) }

    // DataStore
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("shopiq_preferences")
        }
    }

    single<ShopifyTokenLocalDataSource> { ShopifyTokenLocalDataSourceImpl(get()) }

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<ShopifyCustomerRemoteDataSource> {
        ShopifyCustomerRemoteDataSourceImpl(get(named("storefrontApolloClient")))
    }
    single<UserRemoteDataSource> { UserRemoteDataSourceImpl(get()) }

    // Auth
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }

    single {
        AuthRepositoryImpl(get(), get(), get(), get())
    } binds arrayOf(
        AuthRepository::class,
        ShopifyTokenProvider::class
    )

    // Chatbot
    single<ChatbotRepository> { ChatbotRepositoryImpl(get(), get(), androidContext()) }

    // Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<AppDatabase>().favoriteDao() }
    single { get<AppDatabase>().addressDao() }

    // Cart
    single<CartResponseValidator> { CartResponseValidator() }
    single<CartIdDataSource> { CartIdRemoteDataSourceImpl(get(), get()) }
    single<CartRemoteDataSource> {
        CartRemoteDataSourceImpl(
            get(named("storefrontApolloClient")),
            get()
        )
    }
    single<CartRepository> {
        CartRepositoryImpl(get(), get(), get())
    } bind CartRepository::class bind CacheInvalidator::class

    single<OrdersRemoteDataSource> {
        OrdersRemoteDataSourceImpl(
            get(named("storefrontApolloClient")),
        )
    }

    single<OrdersRepository> { OrdersRepositoryImpl(get(), get()) }


    // Other repos
    single<com.iti.data.sources.local.onboarding.OnboardingLocalDataSource> { com.iti.data.sources.local.onboarding.OnboardingLocalDataSourceImpl(get()) }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }

    single<com.iti.data.sources.local.search.SearchHistoryLocalDataSource> { com.iti.data.sources.local.search.SearchHistoryLocalDataSourceImpl(get(), get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single<LocationTracker> { LocationTrackerImpl(get()) }
    single<AddressRepository> { AddressRepositoryImpl(get(), get(), androidContext()) }
    single<CurrencyRemoteDataSource> { CurrencyRemoteDataSourceImpl(get()) }
    single<CurrencyLocalDataSource> { CurrencyLocalDataSourceImpl(get()) }
    single<CurrencyRepository> { CurrencyRepositoryImpl(get(), get()) }
    single<CheckoutRemoteDataSource> { CheckoutRemoteDataSourceImpl(get()) }
    single<CheckoutRepository> { CheckoutRepositoryImpl(get(), get()) }
}