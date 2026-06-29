package com.iti.data.di

import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.products.ProductsRepository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.iti.data.repository.OnboardingRepositoryImpl
import com.iti.domain.repository.OnboardingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.sources.remote.auth.AuthRemoteDataSource
import com.iti.data.sources.remote.auth.AuthRemoteDataSourceImpl
import com.iti.data.repositories.AuthRepositoryImpl
import com.iti.domain.repositories.auth.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
val dataModule = module {
    single { ShopifyNetworkConfig.apolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get()) }
    single<ProductsRepository> { ProductsRepositoryImpl(get()) }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                androidContext().preferencesDataStoreFile("shopiq_preferences")
            }
        )
    }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get()) }
    single { ShopifyNetworkConfig.apolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get()) }
    single<ProductsRepository> { ProductsRepositoryImpl(get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}
