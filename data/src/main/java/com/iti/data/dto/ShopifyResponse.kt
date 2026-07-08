package com.iti.data.dto


data class ShopifyResponse(
    val data: ShopifyData,
    val extensions: Extensions
)

data class ShopifyData(
    val products: ProductConnection? = null,
    val product: ProductNode? = null
)

data class ProductConnection(
    val edges: List<ProductEdge>,
    val pageInfo: PageInfo
)

data class ProductEdge(
    val node: ProductNode
)

data class ProductNode(
    val id: String,
    val title: String,
    val description: String,
    val handle: String,
    val vendor: String,
    val productType: String,
    val tags: List<String>,
    val priceRangeV2: PriceRangeV2,
    val images: ImageConnection,
    val variants: VariantConnection,
    val reviews: List<ReviewDto> = emptyList()
)

data class PriceRangeV2(
    val minVariantPrice: MoneyV2,
    val maxVariantPrice: MoneyV2
)

data class MoneyV2(
    val amount: String,
    val currencyCode: String
)

data class ImageConnection(
    val edges: List<ImageEdge>
)

data class ImageEdge(
    val node: ImageNode
)

data class ImageNode(
    val url: String,
    val altText: String?
)

data class VariantConnection(
    val edges: List<VariantEdge>
)

data class VariantEdge(
    val node: VariantNode
)

data class VariantNode(
    val id: String,
    val title: String,
    val price: MoneyV2,
    val availableForSale: Boolean
)

data class PageInfo(
    val hasNextPage: Boolean,
    val endCursor: String?
)

data class Extensions(
    val cost: QueryCost
)

data class QueryCost(
    val requestedQueryCost: Int,
    val actualQueryCost: Int,
    val throttleStatus: ThrottleStatus
)

data class ThrottleStatus(
    val maximumAvailable: Double,
    val currentlyAvailable: Int,
    val restoreRate: Double
)

data class ReviewDto(
    val id: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val body: String,
    val createdAt: String,
    val approved: Boolean,
    val avatarUrl: String? = null
)