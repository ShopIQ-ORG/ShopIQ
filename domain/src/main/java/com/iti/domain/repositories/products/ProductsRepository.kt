package com.iti.domain.repositories.products

import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.PaginatedProducts
import com.iti.domain.models.Category
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getBrands(): Flow<Result<List<Brand>>>
    fun getAds(): Flow<Result<List<Ad>>>
    fun getProductsByNumber(count: Int = 10): Flow<Result<List<Product>>>
    fun getProductsPaginated(count: Int, after: String? = null): Flow<Result<PaginatedProducts>>
    fun getProductDetails(productId: Long): Flow<Result<Product>>
    fun getMainCategories(): Flow<Result<List<Category>>>
    suspend fun addToFavorites(product: Product)
    suspend fun removeFromFavorites(productId: String)
    fun getFavorites(): Flow<Result<List<Product>>>
    suspend fun isFavorite(productId: String): Boolean

    fun searchProducts(query: String): Flow<Result<List<Product>>>
    fun getPopularProducts(count: Int = 10): Flow<Result<List<Product>>>
    fun getProductsByCategory(categoryId: String, count: Int = 10): Flow<Result<List<Product>>>
    fun getBestSellers(count: Int = 10): Flow<Result<List<Product>>>
}

