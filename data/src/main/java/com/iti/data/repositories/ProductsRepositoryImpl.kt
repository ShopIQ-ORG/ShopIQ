package com.iti.data.repositories

import android.util.Log
import com.iti.data.core.handleException
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.mappers.toDomainAd
import com.iti.data.mappers.toDomainBrand
import com.iti.data.mappers.toDomainProducts
import com.iti.data.mappers.toDomainProduct
import com.iti.data.mappers.toDomainCategories
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.PaginatedProducts
import com.iti.domain.models.Category
import com.iti.domain.models.Money
import com.iti.domain.models.ProductImage
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
            emit(Result.Failure(e.handleException()))
        }
    }

    override fun getProductsPaginated(count: Int, after: String?): Flow<Result<PaginatedProducts>> = flow {
        emit(Result.Loading)
        try {
            val shopifyResponse = remoteDataSource.getProductsByNumber(count, after)
            val domainProducts = shopifyResponse.toDomainProducts()
            val hasNextPage = shopifyResponse.data.products?.pageInfo?.hasNextPage ?: false
            val endCursor = shopifyResponse.data.products?.pageInfo?.endCursor
            emit(
                Result.Success(
                    PaginatedProducts(
                        products = domainProducts,
                        hasNextPage = hasNextPage,
                        endCursor = endCursor
                    )
                )
            )
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
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

    override fun getProductDetails(productId: Long): Flow<Result<Product>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProductDetails(productId)
            val domainProduct = response.toDomainProduct()
            emit(Result.Success(domainProduct))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getMainCategories(): Flow<Result<List<Category>>> = flow {
        emit(Result.Loading)
        try {
            val data = remoteDataSource.getMainCategories()
            val categories = data.toDomainCategories()
            emit(Result.Success(categories))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
