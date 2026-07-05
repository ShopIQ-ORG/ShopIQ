package com.iti.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.iti.data.repositories.*
import com.iti.data.sources.local.room.AppDatabase
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSource
import com.iti.data.sources.local.shopify.ShopifyTokenLocalDataSourceImpl
import com.iti.data.repositories.OrdersRepositoryImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.*
import com.iti.data.sources.remote.orders.OrdersRemoteDataSource
import com.iti.data.sources.remote.orders.OrdersRemoteDataSourceImpl
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSource
import com.iti.data.sources.remote.shopifycustomer.ShopifyCustomerRemoteDataSourceImpl
import com.iti.data.sources.remote.user.UserRemoteDataSource
import com.iti.data.sources.remote.user.UserRemoteDataSourceImpl
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.models.auth.ShopifyCustomerToken
import com.iti.domain.repositories.ai.ChatbotRepository
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.onboarding.OnboardingRepository
import com.iti.domain.repositories.orders.OrdersRepository
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.repositories.search.SearchHistoryRepository
import com.iti.domain.util.CacheInvalidator
import com.iti.domain.util.ShopifyTokenProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val dataModule = module {

    single { Gson() }
    single(named("adminApolloClient")) { ShopifyNetworkConfig.apolloClient }
    single(named("storefrontApolloClient")) { ShopifyNetworkConfig.storefrontApolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get(named("adminApolloClient"))) }
    single<ProductsRepository> { ProductsRepositoryImpl(get(), get(), get()) }

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
        ).build()
    }
    single { get<AppDatabase>().favoriteDao() }

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
        CartRepositoryImpl(get(), get())
    } bind CartRepository::class bind CacheInvalidator::class

    single<OrdersRemoteDataSource> {
        OrdersRemoteDataSourceImpl(
            get(named("storefrontApolloClient")),
        )
    }

    single<OrdersRepository> { OrdersRepositoryImpl(get(), get()) }


    // Other repos
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
}