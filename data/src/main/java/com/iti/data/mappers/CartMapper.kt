package com.iti.data.mappers

import com.iti.data.dto.cart.CartBuyerIdentityDto
import com.iti.data.storefront.CartDiscountCodesUpdateMutation
import com.iti.data.storefront.CartLinesAddMutation
import com.iti.data.storefront.CartLinesUpdateMutation
import com.iti.data.storefront.GetCartQuery
import com.iti.data.dto.cart.CartDto
import com.iti.data.dto.cart.CartLineDto
import com.iti.data.dto.cart.DiscountCodeDto
import com.iti.data.dto.cart.MoneyDto
import com.iti.data.dto.cart.SelectedOptionDto
import com.iti.domain.models.Money
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.cart.CartItem


fun GetCartQuery.Cart.toDto(): CartDto {
    val lineNodes = lines.edges.map { it.node }

    return CartDto(
        id = id,
        lines = lineNodes.mapNotNull { it.toLineDto() },
        discountCodes = discountCodes.map { DiscountCodeDto(it.code, it.applicable) },
        subtotalAmount = MoneyDto(
            cost.subtotalAmount.amount,
            cost.subtotalAmount.currencyCode.toString()
        ),
        totalAmount = MoneyDto(
            cost.totalAmount.amount,
            cost.totalAmount.currencyCode.toString()
        ),
        totalTaxAmount = cost.totalTaxAmount?.let {
            MoneyDto(it.amount, it.currencyCode.toString())
        },
        totalDutyAmount = cost.totalDutyAmount?.let {
            MoneyDto(it.amount, it.currencyCode.toString())
        },
        buyerIdentity = buyerIdentity?.let {
            CartBuyerIdentityDto(
                email = it.email,
                phone = it.phone,
                countryCode = it.countryCode
            )
        }
    )
}

fun GetCartQuery.Node.toLineDto(): CartLineDto? {
    val variant = merchandise.onProductVariant ?: return null
    return CartLineDto(
        id = id,
        quantity = quantity,
        amountPerQuantity = MoneyDto(
            cost.amountPerQuantity.amount,
            cost.amountPerQuantity.currencyCode.toString()
        ),
        subtotalAmount = MoneyDto(
            cost.subtotalAmount.amount,
            cost.subtotalAmount.currencyCode.toString()
        ),
        totalAmount = MoneyDto(
            cost.totalAmount.amount,
            cost.totalAmount.currencyCode.toString()
        ),
        variantId = variant.id,
        variantTitle = variant.title,
        imageUrl = variant.image?.url?.toString()
            ?: variant.product.featuredImage?.url?.toString()
            ?: "",
        imageAltText = variant.image?.altText ?: variant.product.featuredImage?.altText,
        priceAmount = variant.price.amount,
        priceCurrencyCode = variant.price.currencyCode.toString(),
        compareAtPriceAmount = variant.compareAtPrice?.amount,
        selectedOptions = variant.selectedOptions.map {
            SelectedOptionDto(it.name, it.value)
        },
        productId = variant.product.id,
        productTitle = variant.product.title,
        productHandle = variant.product.handle,
        productFeaturedImageUrl = variant.product.featuredImage?.url?.toString(),
        vendor = variant.product.vendor
    )
}

fun CartLinesAddMutation.Cart.toDto(): CartDto {
    val lineNodes = lines.edges.map { it.node }

    return CartDto(
        id = id,
        lines = lineNodes.map {
            CartLineDto(
                id = it.id,
                quantity = it.quantity,
                amountPerQuantity = MoneyDto("0.0", "USD"),
                subtotalAmount = MoneyDto("0.0", "USD"),
                totalAmount = MoneyDto("0.0", "USD"),
                variantId = "",
                variantTitle = "",
                imageUrl = "",
                imageAltText = null,
                priceAmount = "0.0",
                priceCurrencyCode = "USD",
                compareAtPriceAmount = null,
                selectedOptions = emptyList(),
                productId = "",
                productTitle = "",
                productHandle = "",
                productFeaturedImageUrl = null,
                vendor = ""
            )
        },
        discountCodes = emptyList(),
        subtotalAmount = MoneyDto("0.0", "USD"),
        totalAmount = MoneyDto("0.0", "USD"),
        totalTaxAmount = null,
        totalDutyAmount = null,
        buyerIdentity = null
    )
}

fun CartLinesUpdateMutation.Cart.toDto(): CartDto {
    val lineNodes = lines.edges.map { it.node }

    return CartDto(
        id = id,
        lines = lineNodes.map {
            CartLineDto(
                id = it.id,
                quantity = it.quantity,
                amountPerQuantity = MoneyDto("0.0", "USD"),
                subtotalAmount = MoneyDto("0.0", "USD"),
                totalAmount = MoneyDto("0.0", "USD"),
                variantId = "",
                variantTitle = "",
                imageUrl = "",
                imageAltText = null,
                priceAmount = "0.0",
                priceCurrencyCode = "USD",
                compareAtPriceAmount = null,
                selectedOptions = emptyList(),
                productId = "",
                productTitle = "",
                productHandle = "",
                productFeaturedImageUrl = null,
                vendor = ""
            )
        },
        discountCodes = emptyList(),
        subtotalAmount = MoneyDto("0.0", "USD"),
        totalAmount = MoneyDto("0.0", "USD"),
        totalTaxAmount = null,
        totalDutyAmount = null,
        buyerIdentity = null
    )
}

fun CartDiscountCodesUpdateMutation.Cart.toDto(): CartDto {
    return CartDto(
        id = id,
        lines = emptyList(),
        discountCodes = discountCodes.map { DiscountCodeDto(it.code, it.applicable) },
        subtotalAmount = MoneyDto(
            cost.subtotalAmount.amount,
            cost.subtotalAmount.currencyCode.toString()
        ),
        totalAmount = MoneyDto(
            cost.totalAmount.amount,
            cost.totalAmount.currencyCode.toString()
        ),
        totalTaxAmount = null,
        totalDutyAmount = null,
        buyerIdentity = null
    )
}
fun CartDto.toDomain(): Cart {
    return Cart(
        id = id,
        items = lines.map { it.toDomain() },
        discountCodes = discountCodes.filter { it.applicable }.map { it.code },
        subtotal = Money(subtotalAmount.amount, subtotalAmount.currencyCode),
        total = Money(totalAmount.amount, totalAmount.currencyCode),
        totalTax = totalTaxAmount?.let { Money(it.amount, it.currencyCode) }
    )
}

fun CartLineDto.toDomain(): CartItem {
    val variantLabel = selectedOptions.joinToString(" / ") { it.value }
        .ifBlank { variantTitle }

    return CartItem(
        id = id,
        productId = productId,
        variantId = variantId,
        title = productTitle,
        variant = variantLabel,
        price = Money(priceAmount, priceCurrencyCode),
        imageUrl = imageUrl,
        quantity = quantity
    )
}
