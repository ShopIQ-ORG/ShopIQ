package com.iti.data.mappers

import com.iti.data.GetProductsQuery
import com.iti.data.GetProductDetailsQuery
import com.iti.data.dto.*
import com.iti.domain.models.Money
import com.iti.domain.models.Product
import com.iti.domain.models.ProductImage
import com.iti.domain.models.ProductVariant

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
                        amount = edge.node.priceRange.minVariantPrice.amount,
                        currencyCode = edge.node.priceRange.minVariantPrice.currencyCode.name
                    ),
                    maxVariantPrice = MoneyV2(
                        amount = edge.node.priceRange.maxVariantPrice.amount,
                        currencyCode = edge.node.priceRange.maxVariantPrice.currencyCode.name
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
                                    currencyCode = edge.node.priceRange.minVariantPrice.currencyCode.name
                                ),
                                availableForSale = variantEdge.node.availableForSale
                            )
                        )
                    }
                )
            )
        )
    }

    val s = ShopifyResponse(
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

    println(s)

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
                amount = this.priceRange.minVariantPrice.amount,
                currencyCode = this.priceRange.minVariantPrice.currencyCode.name
            ),
            maxVariantPrice = MoneyV2(
                amount = this.priceRange.maxVariantPrice.amount,
                currencyCode = this.priceRange.maxVariantPrice.currencyCode.name
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
                            currencyCode = this.priceRange.minVariantPrice.currencyCode.name
                        ),
                        availableForSale = variantEdge.node.availableForSale
                    )
                )
            }
        )
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
        }
    )
}
