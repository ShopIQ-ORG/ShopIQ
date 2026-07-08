package com.iti.data.repositories

import com.iti.data.utils.handleException
import com.iti.data.sources.remote.ProductsRemoteDataSource
import com.iti.data.mappers.toDomainAd
import com.iti.data.mappers.toDomainBrand
import com.iti.data.mappers.toDomainProducts
import com.iti.data.mappers.toDomainProduct
import com.iti.data.mappers.toDomainCategories
import com.iti.data.mappers.toFavoriteEntity
import com.iti.data.sources.local.room.FavoriteDao
import com.iti.domain.models.User
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.PaginatedProducts
import com.iti.domain.models.Category
import com.iti.domain.models.Result
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.products.ProductsRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.iti.data.sources.local.room.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class ProductsRepositoryImpl(
    private val remoteDataSource: ProductsRemoteDataSource,
    private val favoriteDao: FavoriteDao,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val gson: com.google.gson.Gson
) : ProductsRepository {

    override fun getProductsByNumber(count: Int): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val shopifyResponse = remoteDataSource.getProductsByNumber(count)
            val domainProducts = shopifyResponse.toDomainProducts()
            emit(Result.Success(domainProducts))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
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
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    companion object {
        private val EXCLUDED_CATEGORIES = setOf(
            "WOMEN", "MEN", "KID", "KIDS", "SALE", "HOME",
            "ACCESSORIES", "NEW ARRIVALS", "BEST SELLERS",
            "FEATURED", "ALL", "COLLECTION", "COLLECTIONS",
            "HYDROGEN", "AUTOMATED COLLECTION", "HOME PAGE"
        )
    }

    override fun getBrands(): Flow<Result<List<Brand>>> = flow {
        emit(Result.Loading)
        try {
            val brands = remoteDataSource.getBrands()
                .map { it.toDomainBrand() }
                .filter { brand ->
                    brand.name.uppercase().trim() !in EXCLUDED_CATEGORIES
                }
            emit(Result.Success(brands))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getAds(): Flow<Result<List<Ad>>> = flow {
        emit(Result.Loading)
        try {
            val ads = remoteDataSource.getAds().map { it.toDomainAd() }
            emit(Result.Success(ads))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
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
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
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
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override suspend fun addToFavorites(product: Product) {
        val user = getCurrentUser()
        if (user is User.AuthenticatedUser) {
            val userId = user.uid
            val cleanId = product.id.substringAfterLast("/")
            // Write to local database cache immediately (instant UI update)
            favoriteDao.insertFavorite(product.toFavoriteEntity(userId).copy(productId = cleanId))

            // Sync to Firestore: list of product ID strings in favorites field under users/{userId} document
            try {
                firestore.collection("users").document(userId)
                    .set(
                        mapOf("favorites" to com.google.firebase.firestore.FieldValue.arrayUnion(cleanId)),
                        com.google.firebase.firestore.SetOptions.merge()
                    ).await()
                android.util.Log.d("ProductsRepository", "Firestore addToFavorites OK: cleanId=$cleanId")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("ProductsRepository", "Firestore addToFavorites failed", e)
            }
        }
    }

    override suspend fun removeFromFavorites(productId: String) {
        val user = getCurrentUser()
        if (user is User.AuthenticatedUser) {
            val userId = user.uid
            val cleanId = productId.substringAfterLast("/")
            // Delete from local database cache immediately
            favoriteDao.deleteFavorite(cleanId, userId)

            // Sync to Firestore: remove cleanId from favorites field array under users/{userId} document
            try {
                firestore.collection("users").document(userId)
                    .set(
                        mapOf("favorites" to com.google.firebase.firestore.FieldValue.arrayRemove(cleanId)),
                        com.google.firebase.firestore.SetOptions.merge()
                    ).await()
                android.util.Log.d("ProductsRepository", "Firestore removeFromFavorites OK: cleanId=$cleanId")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("ProductsRepository", "Firestore removeFromFavorites failed", e)
            }
        }
    }

    override fun getFavorites(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        val user = getCurrentUser()

        if (user is User.AuthenticatedUser) {
            val userId = user.uid

            // 1) Emit Room data IMMEDIATELY so the UI is instant (no waiting for network)
            val localList = favoriteDao.getAllFavorites(userId).first()
            emit(Result.Success(localList.map { it.toDomainProduct() }))

            // 2) Background Firestore sync (only for authenticated users)
            try {
                val docSnapshot = firestore.collection("users").document(userId).get().await()
                val favoriteIds = (docSnapshot.get("favorites") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                
                // Fetch actual product details from Shopify for each ID in parallel
                val favorites = coroutineScope {
                    favoriteIds.map { cleanId ->
                        async {
                            try {
                                val longId = cleanId.toLongOrNull()
                                if (longId != null) {
                                    val response = remoteDataSource.getProductDetails(longId)
                                    val product = response.toDomainProduct()
                                    FavoriteEntity(
                                        productId = cleanId,
                                        userId = userId,
                                        title = product.title,
                                        price = "${product.minPrice.amount} ${product.minPrice.currencyCode}",
                                        imageUrl = product.images.firstOrNull()?.url.orEmpty()
                                    )
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("ProductsRepository", "Failed to fetch product details from Shopify for ID: $cleanId", e)
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                // Refresh local Room cache from Firestore
                favoriteDao.deleteFavoritesForUser(userId)
                favorites.forEach { favoriteDao.insertFavorite(it) }
                android.util.Log.d("ProductsRepository", "Firestore getFavorites synced ${favorites.size} items from favorites list for users/$userId")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("ProductsRepository", "Firestore getFavorites failed", e)
            }

            // 3) Keep streaming Room updates reactively after Firestore sync
            favoriteDao.getAllFavorites(userId).collect { list ->
                emit(Result.Success(list.map { it.toDomainProduct() }))
            }
        } else {
            // For guests, emit empty list since favorites are disabled
            emit(Result.Success(emptyList()))
        }
    }

    override suspend fun isFavorite(productId: String): Boolean {
        val user = getCurrentUser()
        return if (user is User.AuthenticatedUser) {
            val cleanId = productId.substringAfterLast("/")
            favoriteDao.isFavorite(cleanId, user.uid)
        } else {
            false
        }
    }

    private suspend fun getCurrentUser(): User {
        val userRes = authRepository.getCurrentUser()
        return if (userRes is Result.Success) userRes.data else User.GuestUser
    }

    override fun searchProducts(query: String): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProducts(first = 20, query = query)
            emit(Result.Success(response.toDomainProducts()))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
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
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    override fun getProductsByCategory(categoryId: String, count: Int): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        try {
            val response = remoteDataSource.getProductsByCategory(categoryId, count)
            emit(Result.Success(response.toDomainProducts()))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e.handleException()))
        }
    }

    override fun getBestSellers(count: Int): Flow<Result<List<Product>>> {
        return getProductsByCategory("gid://shopify/Collection/493787218155", count)
    }

    override fun addProductReview(
        productId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        avatarUrl: String?
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val numericId = productId.substringAfterLast("/").toLongOrNull() 
                ?: throw Exception("Invalid product ID: $productId")
            val productDetails = remoteDataSource.getProductDetails(numericId)
            val existingIds = productDetails.data.product?.reviews?.map { it.id } ?: emptyList()

            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
            formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val createdAt = formatter.format(java.util.Date())

            val newReviewId = remoteDataSource.createProductReview(
                productId = productId,
                customerName = customerName,
                rating = rating,
                title = title,
                body = body,
                createdAt = createdAt,
                avatarUrl = avatarUrl ?: ""
            )

            val updatedIds = existingIds + newReviewId
            remoteDataSource.setProductReviews(productId, updatedIds)

            emit(Result.Success(Unit))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun updateProductReview(
        reviewId: String,
        customerName: String,
        rating: Int,
        title: String,
        body: String,
        avatarUrl: String?
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
            formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val createdAt = formatter.format(java.util.Date())

            remoteDataSource.updateProductReview(
                reviewId = reviewId,
                customerName = customerName,
                rating = rating,
                title = title,
                body = body,
                createdAt = createdAt,
                avatarUrl = avatarUrl ?: ""
            )

            emit(Result.Success(Unit))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun deleteProductReview(
        productId: String,
        reviewId: String
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            val numericId = productId.substringAfterLast("/").toLongOrNull()
                ?: throw Exception("Invalid product ID: $productId")
            val productDetails = remoteDataSource.getProductDetails(numericId)
            val existingIds = productDetails.data.product?.reviews?.map { it.id } ?: emptyList()

            val updatedIds = existingIds.filter { it != reviewId }

            remoteDataSource.deleteProductReview(reviewId)
            remoteDataSource.setProductReviews(productId, updatedIds)

            emit(Result.Success(Unit))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    override fun getProductTranslations(productId: String): Flow<Result<Map<String, String>>> = flow {
        emit(Result.Loading)
        try {
            val translations = remoteDataSource.getProductTranslations(productId)
            emit(Result.Success(translations))
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}
