package com.iti.data.mappers

import com.iti.data.dto.cart.CartBuyerIdentityDto
import com.iti.data.dto.cart.CartDto
import com.iti.data.dto.cart.CartLineDto
import com.iti.data.dto.cart.DiscountCodeDto
import com.iti.data.dto.cart.MoneyDto
import com.iti.data.dto.cart.SelectedOptionDto
import com.iti.data.storefront.fragment.CartFields
import com.iti.domain.models.Money
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.cart.CartItem

fun CartFields.toDto(): CartDto {
    return CartDto(
        id = id,
        lines = lines.edges.mapNotNull { edge ->
            val variant = edge.node.merchandise.onProductVariant ?: return@mapNotNull null

            CartLineDto(
                id = edge.node.id,
                quantity = edge.node.quantity,

                amountPerQuantity = MoneyDto(
                    amount = edge.node.cost.amountPerQuantity.amount,
                    currencyCode = edge.node.cost.amountPerQuantity.currencyCode.toString()
                ),

                subtotalAmount = MoneyDto(
                    amount = edge.node.cost.subtotalAmount.amount,
                    currencyCode = edge.node.cost.subtotalAmount.currencyCode.toString()
                ),

                totalAmount = MoneyDto(
                    amount = edge.node.cost.totalAmount.amount,
                    currencyCode = edge.node.cost.totalAmount.currencyCode.toString()
                ),

                variantId = variant.id,
                variantTitle = variant.title,

                imageUrl = variant.image?.url?.toString()
                    ?: variant.product.featuredImage?.url?.toString()
                    ?: "",

                imageAltText = variant.image?.altText
                    ?: variant.product.featuredImage?.altText,

                priceAmount = variant.price.amount,
                priceCurrencyCode = variant.price.currencyCode.toString(),

                compareAtPriceAmount = variant.compareAtPrice?.amount,

                selectedOptions = variant.selectedOptions.map {
                    SelectedOptionDto(
                        name = it.name,
                        value = it.value
                    )
                },

                productId = variant.product.id,
                productTitle = variant.product.title,
                productHandle = variant.product.handle,
                productFeaturedImageUrl = variant.product.featuredImage?.url?.toString(),
                vendor = variant.product.vendor.orEmpty()
            )
        },

        discountCodes = discountCodes.map {
            DiscountCodeDto(
                code = it.code,
                applicable = it.applicable
            )
        },

        subtotalAmount = MoneyDto(
            amount = cost.subtotalAmount.amount,
            currencyCode = cost.subtotalAmount.currencyCode.toString()
        ),

        totalAmount = MoneyDto(
            amount = cost.totalAmount.amount,
            currencyCode = cost.totalAmount.currencyCode.toString()
        ),

        totalTaxAmount = cost.totalTaxAmount?.let {
            MoneyDto(
                amount = it.amount,
                currencyCode = it.currencyCode.toString()
            )
        },

        totalDutyAmount = cost.totalDutyAmount?.let {
            MoneyDto(
                amount = it.amount,
                currencyCode = it.currencyCode.toString()
            )
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

fun CartDto.toDomain(): Cart {
    val discountAmount = computeDiscountAmount()

    return Cart(
        id = id,
        items = lines.map { it.toDomain() },
        discountCodes = discountCodes.filter { it.applicable }.map { it.code },
        discountAmount = discountAmount,
        subtotal = Money(subtotalAmount.amount, subtotalAmount.currencyCode),
        total = Money(totalAmount.amount, totalAmount.currencyCode),
        totalTax = totalTaxAmount?.let { Money(it.amount, it.currencyCode) }
    )
}

private fun CartDto.computeDiscountAmount(): Money? {
    if (discountCodes.none { it.applicable }) return null

    val totalDiscount = lines.sumOf { line ->
        val perQuantity = line.amountPerQuantity.amount.toDoubleOrNull() ?: 0.0
        val quantity = line.quantity
        val lineTotal = line.totalAmount.amount.toDoubleOrNull() ?: 0.0
        val expectedFullPrice = perQuantity * quantity
        (expectedFullPrice - lineTotal).coerceAtLeast(0.0)
    }

    if (totalDiscount <= 0.0) return null

    return Money(
        "%.2f".format(totalDiscount),
        subtotalAmount.currencyCode
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