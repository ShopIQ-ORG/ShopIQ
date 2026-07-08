package com.iti.data.mappers

import com.iti.data.GetAllCategoriesQuery
import com.iti.data.GetProductDetailsQuery
import com.iti.data.GetProductsInCollectionQuery
import com.iti.data.GetProductsQuery
import com.iti.data.dto.*
import com.iti.domain.models.*
import com.iti.domain.models.Money
import com.iti.domain.models.Product
import com.iti.domain.models.ProductImage
import com.iti.domain.models.ProductVariant
import com.iti.domain.models.Category
import com.iti.data.utils.BrandImageMapper

fun GetProductsQuery.Data.toShopifyResponse(): ShopifyResponse {
    val productEdges = this.products.edges.map { edge ->
        ProductEdge(
            node = ProductNode(
                id = edge.node.id,
                title = edge.node.title,
                description = edge.node.description,
                handle = edge.node.handle,
                vendor = edge.node.vendor,
                productType = edge.node.productType,
                tags = edge.node.tags,
                priceRangeV2 = PriceRangeV2(
                    minVariantPrice = MoneyV2(
                        amount = edge.node.priceRangeV2.minVariantPrice.amount,
                        currencyCode = edge.node.priceRangeV2.minVariantPrice.currencyCode.name
                    ),
                    maxVariantPrice = MoneyV2(
                        amount = edge.node.priceRangeV2.maxVariantPrice.amount,
                        currencyCode = edge.node.priceRangeV2.maxVariantPrice.currencyCode.name
                    )
                ),
                images = ImageConnection(
                    edges = edge.node.images.edges.map { imageEdge ->
                        ImageEdge(
                            node = ImageNode(
                                url = imageEdge.node.url.toString(),
                                altText = imageEdge.node.altText
                            )
                        )
                    }
                ),
                variants = VariantConnection(
                    edges = edge.node.variants.edges.map { variantEdge ->
                        VariantEdge(
                            node = VariantNode(
                                id = variantEdge.node.id,
                                title = variantEdge.node.title,
                                price = MoneyV2(
                                    amount = variantEdge.node.price.toString(),
                                    currencyCode = edge.node.priceRangeV2.minVariantPrice.currencyCode.name
                                ),
                                availableForSale = variantEdge.node.availableForSale
                            )
                        )
                    }
                )
            )
        )
    }

    return ShopifyResponse(
        data = ShopifyData(
            products = ProductConnection(
                edges = productEdges,
                pageInfo = PageInfo(
                    hasNextPage = this.products.pageInfo.hasNextPage,
                    endCursor = this.products.pageInfo.endCursor
                )
            )
        ),
        extensions = Extensions(
            cost = QueryCost(
                requestedQueryCost = 0,
                actualQueryCost = 0,
                throttleStatus = ThrottleStatus(0.0, 0, 0.0)
            )
        )
    )
}

fun ShopifyResponse.toDomainProducts(): List<Product> {
    return this.data.products?.edges?.map { edge ->
        val node = edge.node
        Product(
            id = node.id,
            title = node.title,
            description = node.description,
            handle = node.handle,
            productType = node.productType,
            vendor = node.vendor,
            tags = node.tags,
            minPrice = Money(
                amount = node.priceRangeV2.minVariantPrice.amount,
                currencyCode = node.priceRangeV2.minVariantPrice.currencyCode
            ),
            maxPrice = Money(
                amount = node.priceRangeV2.maxVariantPrice.amount,
                currencyCode = node.priceRangeV2.maxVariantPrice.currencyCode
            ),
            images = node.images.edges.map { imageEdge ->
                ProductImage(
                    url = imageEdge.node.url,
                    altText = imageEdge.node.altText
                )
            },
            variants = node.variants.edges.map { variantEdge ->
                ProductVariant(
                    id = variantEdge.node.id,
                    title = variantEdge.node.title,
                    price = Money(
                        amount = variantEdge.node.price.amount,
                        currencyCode = variantEdge.node.price.currencyCode
                    ),
                    availableForSale = variantEdge.node.availableForSale
                )
            }
        )
    } ?: emptyList()
}

fun GetProductDetailsQuery.Product.toShopifyResponse(): ShopifyResponse {
    val productNode = ProductNode(
        id = this.id,
        title = this.title,
        description = this.description,
        handle = this.handle,
        vendor = this.vendor,
        productType = this.productType,
        tags = this.tags,
        priceRangeV2 = PriceRangeV2(
            minVariantPrice = MoneyV2(
                amount = this.priceRangeV2.minVariantPrice.amount,
                currencyCode = this.priceRangeV2.minVariantPrice.currencyCode.name
            ),
            maxVariantPrice = MoneyV2(
                amount = this.priceRangeV2.maxVariantPrice.amount,
                currencyCode = this.priceRangeV2.maxVariantPrice.currencyCode.name
            )
        ),
        images = ImageConnection(
            edges = this.images.edges.map { imageEdge ->
                ImageEdge(
                    node = ImageNode(
                        url = imageEdge.node.url.toString(),
                        altText = imageEdge.node.altText
                    )
                )
            }
        ),
        variants = VariantConnection(
            edges = this.variants.edges.map { variantEdge ->
                VariantEdge(
                    node = VariantNode(
                        id = variantEdge.node.id,
                        title = variantEdge.node.title,
                        price = MoneyV2(
                            amount = variantEdge.node.price.toString(),
                            currencyCode = this.priceRangeV2.minVariantPrice.currencyCode.name
                        ),
                        availableForSale = variantEdge.node.availableForSale
                    )
                )
            }
        ),
        reviews = this.metafield?.references?.edges?.mapNotNull { edge ->
            val metaobject = edge.node.onMetaobject
            if (metaobject != null) {
                ReviewDto(
                    id = metaobject.id,
                    customerName = metaobject.customerName?.value ?: "Anonymous",
                    rating = metaobject.rating?.value?.toIntOrNull() ?: 5,
                    title = metaobject.title?.value ?: "",
                    body = metaobject.body?.value ?: "",
                    createdAt = metaobject.createdAt?.value ?: "",
                    approved = metaobject.approved?.value?.lowercase() == "true",
                    avatarUrl = metaobject.avatarUrl?.value
                )
            } else null
        } ?: emptyList()
    )

    return ShopifyResponse(
        data = ShopifyData(
            product = productNode
        ),
        extensions = Extensions(
            cost = QueryCost(
                requestedQueryCost = 0,
                actualQueryCost = 0,
                throttleStatus = ThrottleStatus(0.0, 0, 0.0)
            )
        )
    )
}

fun ShopifyResponse.toDomainProduct(): Product {
    val node = this.data.product ?: throw Exception("Product details data is null")
    return Product(
        id = node.id,
        title = node.title,
        description = node.description,
        handle = node.handle,
        productType = node.productType,
        vendor = node.vendor,
        tags = node.tags,
        minPrice = Money(
            amount = node.priceRangeV2.minVariantPrice.amount,
            currencyCode = node.priceRangeV2.minVariantPrice.currencyCode
        ),
        maxPrice = Money(
            amount = node.priceRangeV2.maxVariantPrice.amount,
            currencyCode = node.priceRangeV2.maxVariantPrice.currencyCode
        ),
        images = node.images.edges.map { imageEdge ->
            ProductImage(
                url = imageEdge.node.url,
                altText = imageEdge.node.altText
            )
        },
        variants = node.variants.edges.map { variantEdge ->
            ProductVariant(
                id = variantEdge.node.id,
                title = variantEdge.node.title,
                price = Money(
                    amount = variantEdge.node.price.amount,
                    currencyCode = variantEdge.node.price.currencyCode
                ),
                availableForSale = variantEdge.node.availableForSale
            )
        },
        reviews = node.reviews.map { review ->
            com.iti.domain.models.ProductReview(
                id = review.id,
                customerName = review.customerName,
                rating = review.rating,
                title = review.title,
                body = review.body,
                createdAt = review.createdAt,
                approved = review.approved,
                avatarUrl = review.avatarUrl
            )
        }
    )
}

fun GetAllCategoriesQuery.Data.toDomainCategories(): List<Category> {
    return this.collections.nodes.map { node ->
        Category(
            id = node.id,
            title = node.title,
            itemCount = 0,
            imageAssetPath = node.image?.url?.toString() ?: ""
        )
    }
}

fun GetProductsInCollectionQuery.Data.toDomainProducts(): List<Product> {
    return this.collection?.products?.edges?.map { edge ->
        val node = edge.node
        val minPrice = Money(
            amount = node.priceRangeV2.minVariantPrice.amount,
            currencyCode = node.priceRangeV2.minVariantPrice.currencyCode.name
        )
        Product(
            id = node.id,
            title = node.title,
            description = "",
            handle = "",
            productType = node.productType,
            vendor = "",
            tags = emptyList(),
            minPrice = minPrice,
            maxPrice = minPrice,
            images = node.images.edges.map { imageEdge ->
                ProductImage(
                    url = imageEdge.node.url.toString(),
                    altText = null
                )
            },
            variants = emptyList()
        )
    } ?: emptyList()
}

fun BrandDto.toDomainBrand(): Brand {
    return Brand(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
        mappedImageUrl = BrandImageMapper.getBrandImageUrl(this.name, this.imageUrl)
    )
}

fun AdDto.toDomainAd(): Ad {
    return Ad(
        id = this.id,
        imageUrl = this.imageUrl,
        title = this.title,
        subtitle = this.subtitle
    )
}
