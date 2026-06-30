package com.iti.data.di

import com.iti.data.repositories.AuthRepositoryImpl
import com.iti.data.repositories.CartRepositoryImpl
import com.iti.data.repositories.OnboardingRepositoryImpl
import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSourceImpl
import com.iti.data.sources.remote.cart.CartRemoteDataSource
import com.iti.data.sources.remote.cart.CartRemoteDataSourceImpl
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.cart.CartRepository
import com.iti.domain.repositories.onboarding.OnboardingRepository
import com.iti.domain.repositories.products.ProductsRepository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.sources.remote.cart.CartIdDataSource
import com.iti.data.sources.remote.cart.CartIdRemoteDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.qualifier.named

val dataModule = module {
    single(named("adminApolloClient")) { ShopifyNetworkConfig.apolloClient }
    single(named("storefrontApolloClient")) { ShopifyNetworkConfig.storefrontApolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get(named("adminApolloClient"))) }
    single<ProductsRepository> { ProductsRepositoryImpl(get()) }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile("shopiq_preferences")
            }
        )
    }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }


    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    single<CartIdDataSource> { CartIdRemoteDataSourceImpl(get(), get()) }
    single<CartRemoteDataSource> { CartRemoteDataSourceImpl(get(named("storefrontApolloClient"))) }
    single<CartRepository> { CartRepositoryImpl(get(), get()) }
}
