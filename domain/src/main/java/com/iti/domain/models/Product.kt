package com.iti.domain.models

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val handle: String,
    val productType: String,
    val vendor: String,
    val tags: List<String>,
    val minPrice: Money,
    val maxPrice: Money,
    val images: List<ProductImage>,
    val variants: List<ProductVariant>,
    val isFavorite: Boolean = false,
    val reviews: List<ProductReview> = emptyList(),
    // Arabic translations – populated when locale is AR
    val arTitle: String? = null,
    val arDescription: String? = null
)

data class ProductReview(
    val id: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val body: String,
    val createdAt: String,
    val approved: Boolean,
    val avatarUrl: String? = null
)

data class Money(
    val amount: String,
    val currencyCode: String
)

data class ProductImage(
    val url: String,
    val altText: String?
)

data class ProductVariant(
    val id: String,
    val title: String,
    val price: Money,
    val availableForSale: Boolean
)

data class PaginatedProducts(
    val products: List<Product>,
    val hasNextPage: Boolean,
    val endCursor: String?
)
