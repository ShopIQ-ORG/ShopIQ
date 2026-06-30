package com.iti.data.repositories

import android.util.Log
import com.iti.data.core.handleException
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.mappers.toDomainAd
import com.iti.data.mappers.toDomainBrand
import com.iti.data.mappers.toDomainProducts
import com.iti.data.mappers.toDomainProduct
import com.iti.data.mappers.toDomainCategories
import com.iti.data.mappers.toFavoriteEntity
import com.iti.data.sources.local.FavoriteDao
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Category
import com.iti.domain.models.Money
import com.iti.domain.models.ProductImage
import com.iti.domain.models.Result
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryImpl(
    private val remoteDataSource: ProductsRemoteDataSource,
    private val favoriteDao: FavoriteDao
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



    override suspend fun addToFavorites(product: Product) {
        favoriteDao.insertFavorite(product.toFavoriteEntity())
    }

    override suspend fun removeFromFavorites(productId: String) {
        favoriteDao.deleteFavorite(productId)
    }

    override fun getFavorites(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            favoriteDao.getAllFavorites().collect { list ->
                emit(Result.Success(list.map { it.toDomainProduct() }))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override suspend fun isFavorite(productId: String): Boolean {
        return favoriteDao.isFavorite(productId)
    }
}
