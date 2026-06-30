package com.iti.data.mappers

import com.iti.data.sources.local.FavoriteEntity
import com.iti.domain.models.Product
import com.iti.domain.models.Money
import com.iti.domain.models.ProductImage

fun Product.toFavoriteEntity(userId: String): FavoriteEntity {
    return FavoriteEntity(
        productId = this.id,
        userId = userId,
        title = this.title,
        price = this.minPrice.amount,
        imageUrl = this.images.firstOrNull()?.url ?: ""
    )
}

fun FavoriteEntity.toDomainProduct(): Product {
    return Product(
        id = this.productId,
        title = this.title,
        description = "",
        handle = "",
        productType = "",
        vendor = "",
        tags = emptyList(),
        minPrice = Money(amount = this.price, currencyCode = "EGP"),
        maxPrice = Money(amount = this.price, currencyCode = "EGP"),
        images = if (this.imageUrl.isNotEmpty()) listOf(ProductImage(url = this.imageUrl, altText = null)) else emptyList(),
        variants = emptyList(),
        isFavorite = true
    )
}