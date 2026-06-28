package com.iti.data.repositories

import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.mappers.toDomainAd
import com.iti.data.mappers.toDomainBrand
import com.iti.data.mappers.toDomainProducts
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryImpl(
    private val remoteDataSource: ProductsRemoteDataSource
) : ProductsRepository {

    override fun getProductsByNumber(count: Int): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val shopifyResponse = remoteDataSource.getProductsByNumber(count)
            val domainProducts = shopifyResponse.toDomainProducts()
            emit(Result.Success(domainProducts))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getBrands(): Flow<Result<List<Brand>>> = flow {
        emit(Result.Loading)
        try {
            val brands = remoteDataSource.getBrands().map { it.toDomainBrand() }
            emit(Result.Success(brands))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getAds(): Flow<Result<List<Ad>>> = flow {
        emit(Result.Loading)
        try {
            val ads = remoteDataSource.getAds().map { it.toDomainAd() }
            emit(Result.Success(ads))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
