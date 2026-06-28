package com.iti.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.iti.data.repository.OnboardingRepositoryImpl
import com.iti.domain.repository.OnboardingRepository
import org.koin.android.ext.koin.androidContext
import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.sources.remote.CategoryRemoteDataSource
import com.iti.data.sources.remote.CategoryRemoteDataSourceImpl
import com.iti.data.repositories.CategoriesRepositoryImpl
import com.iti.domain.repositories.categories.CategoriesRepository
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.products.ProductsRepository
import org.koin.dsl.module
val dataModule = module {
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
    single<CategoryRemoteDataSource> { CategoryRemoteDataSourceImpl(get()) }
    single<CategoriesRepository> { CategoriesRepositoryImpl(get()) }
}
