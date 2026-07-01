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
    val isFavorite: Boolean = false
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
