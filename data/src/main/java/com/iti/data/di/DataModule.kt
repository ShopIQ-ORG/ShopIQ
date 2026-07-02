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
import com.iti.data.repositories.AuthRepositoryImpl
import com.iti.data.repositories.CartRepositoryImpl
import com.iti.data.repositories.LocationTrackerImpl
import com.iti.data.repositories.OnboardingRepositoryImpl
import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.repositories.SearchHistoryRepositoryImpl
import com.iti.data.sources.local.AppDatabase
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartIdDataSource
import com.iti.data.sources.remote.cart.CartIdRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.data.sources.remote.cart.CartRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartResponseValidator
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.location.LocationTracker
import com.iti.domain.repositories.onboarding.OnboardingRepository
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.repositories.search.SearchHistoryRepository
import com.iti.data.repositories.AddressRepositoryImpl
import com.iti.domain.repositories.address.AddressRepository
import com.iti.domain.util.CacheInvalidator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

import org.koin.core.qualifier.named
import org.koin.dsl.bind

val dataModule = module {
    single { Gson() }
    single(named("adminApolloClient")) { ShopifyNetworkConfig.apolloClient }
    single(named("storefrontApolloClient")) { ShopifyNetworkConfig.storefrontApolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get(named("adminApolloClient"))) }
    single<ProductsRepository> { ProductsRepositoryImpl(get(), get(), get()) }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile("shopiq_preferences")
            }
        )
    }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }
    single { get<AppDatabase>().favoriteDao() }

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

    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single<LocationTracker> { LocationTrackerImpl(get(), androidContext()) }
    single<AddressRepository> { AddressRepositoryImpl() }
}
