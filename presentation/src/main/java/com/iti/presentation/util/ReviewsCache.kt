package com.iti.presentation.util

import com.iti.domain.models.ProductReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory cache that stores the latest reviews per product ID.
 * Written by [ProductDetailsViewModel] when reviews are submitted/edited/deleted.
 * Read by [ProductCard] to overlay fresh review data on top of the stale list-view data.
 */
object ReviewsCache {

    private val _cache = MutableStateFlow<Map<String, List<ProductReview>>>(emptyMap())
    val cache: StateFlow<Map<String, List<ProductReview>>> = _cache.asStateFlow()

    /** Stores the full review list for a product (keyed by numeric product ID). */
    fun updateReviews(productId: String, reviews: List<ProductReview>) {
        val cleanId = productId.substringAfterLast("/")
        _cache.value = _cache.value + (cleanId to reviews)
    }

    /** Returns latest reviews for a product, or null if not cached yet. */
    fun getReviews(productId: String): List<ProductReview>? {
        val cleanId = productId.substringAfterLast("/")
        return _cache.value[cleanId]
    }
}
