package com.iti.data.repositories

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
import com.iti.domain.models.PaginatedProducts
import com.iti.domain.models.Category
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.products.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryImpl(
    private val remoteDataSource: ProductsRemoteDataSource,
    private val favoriteDao: FavoriteDao,
    private val authRepository: AuthRepository
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

    override suspend fun addToFavorites(product: Product) {
        val userId = getUserId()
        favoriteDao.insertFavorite(product.toFavoriteEntity(userId))
    }

    override suspend fun removeFromFavorites(productId: String) {
        val userId = getUserId()
        favoriteDao.deleteFavorite(productId, userId)
    }

    override fun getFavorites(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val userId = getUserId()
            favoriteDao.getAllFavorites(userId).collect { list ->
                emit(Result.Success(list.map { it.toDomainProduct() }))
            }
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override suspend fun isFavorite(productId: String): Boolean {
        val userId = getUserId()
        return favoriteDao.isFavorite(productId, userId)
    }

    private fun getUserId(): String {
        return authRepository.getUserId() ?: "guest"
    }

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProducts(first = 20, query = query)
            emit(Result.Success(response.toDomainProducts()))
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    override fun getPopularProducts(count: Int): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProducts(
                first = count,
                sortKey = com.iti.data.type.ProductSortKeys.BEST_SELLING
            )
            emit(Result.Success(response.toDomainProducts()))
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    override fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProductsByCategory(categoryId)
            emit(Result.Success(response.toDomainProducts()))
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }
}
