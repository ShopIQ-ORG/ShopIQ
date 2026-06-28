package com.iti.data.di

import com.iti.data.repositories.ProductsRepositoryImpl
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.sources.remote.ProductsRemoteDataSourceImpl
import com.iti.data.utils.ShopifyNetworkConfig
import com.iti.domain.repositories.products.ProductsRepository
import org.koin.dsl.module

val dataModule = module {
    single { ShopifyNetworkConfig.apolloClient }
    single<ProductsRemoteDataSource> { ProductsRemoteDataSourceImpl(get()) }
    single<ProductsRepository> { ProductsRepositoryImpl(get()) }
}
