package com.iti.data.repositories

import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.mappers.toDomainProducts
import com.iti.data.mappers.toDomainProduct
import com.iti.domain.models.Product
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
}
