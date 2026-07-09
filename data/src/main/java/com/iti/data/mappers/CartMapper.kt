package com.iti.data.mappers

import com.iti.data.dto.cart.CartBuyerIdentityDto
import com.iti.data.dto.cart.CartDeliveryGroupDto
import com.iti.data.dto.cart.CartDeliveryOptionDto
import com.iti.data.dto.cart.CartDto
import com.iti.data.dto.cart.CartLineDto
import com.iti.data.dto.cart.DiscountCodeDto
import com.iti.data.dto.cart.MoneyDto
import com.iti.data.dto.cart.SelectedOptionDto
import com.iti.data.storefront.fragment.CartFields
import com.iti.domain.models.Money
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.cart.CartItem
import java.util.Locale

fun CartFields.toDto(): CartDto {
    return CartDto(
        id = id,
        checkoutUrl = checkoutUrl.toString(),
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
                vendor = variant.product.vendor.orEmpty(),
                isAvailableForSale = variant.availableForSale,
                quantityAvailable = variant.quantityAvailable ?: 0,
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
        },

        deliveryGroups = deliveryGroups.edges.map { edge ->
            CartDeliveryGroupDto(
                id = edge.node.id,
                selectedDeliveryOption = edge.node.selectedDeliveryOption?.let {
                    CartDeliveryOptionDto(
                        handle = it.handle,
                        title = it.title,
                        estimatedCostAmount = MoneyDto(
                            amount = it.estimatedCost.amount,
                            currencyCode = it.estimatedCost.currencyCode.toString()
                        )
                    )
                },
                deliveryOptions = edge.node.deliveryOptions.map {
                    CartDeliveryOptionDto(
                        handle = it.handle,
                        title = it.title,
                        estimatedCostAmount = MoneyDto(
                            amount = it.estimatedCost.amount,
                            currencyCode = it.estimatedCost.currencyCode.toString()
                        )
                    )
                }
            )
        },
    )
}

fun CartDto.toDomain(): Cart {
    val discountAmount = computeDiscountAmount()
    val shippingAmount = computeShippingAmount()

    return Cart(
        id = id,
        checkoutUrl = checkoutUrl,
        items = lines.map { it.toDomain() },
        discountCodes = discountCodes.filter { it.applicable }.map { it.code },
        discountAmount = discountAmount,
        subtotal = Money(subtotalAmount.amount, subtotalAmount.currencyCode),
        total = Money(totalAmount.amount, totalAmount.currencyCode),
        totalTax = totalTaxAmount?.let { Money(it.amount, it.currencyCode) },
        shippingAmount = shippingAmount
    )
}

private fun CartDto.computeDiscountAmount(): Money? {
    if (discountCodes.none { it.applicable }) return null

    val subtotal = subtotalAmount.amount.toDoubleOrNull() ?: 0.0
    val total = totalAmount.amount.toDoubleOrNull() ?: 0.0
    val tax = totalTaxAmount?.amount?.toDoubleOrNull() ?: 0.0
    val shipping = deliveryGroups
        .mapNotNull { it.selectedDeliveryOption }
        .sumOf { it.estimatedCostAmount.amount.toDoubleOrNull() ?: 0.0 }

    val discount = subtotal + tax + shipping - total

    if (discount <= 0.0) return null

    return Money(
        String.format(Locale.US, "%.2f", discount),
        subtotalAmount.currencyCode
    )
}

private fun CartDto.computeShippingAmount(): Money? {
    val selectedOptions = deliveryGroups.mapNotNull { it.selectedDeliveryOption }
    if (selectedOptions.isEmpty()) return null

    val totalShipping = selectedOptions.sumOf {
        it.estimatedCostAmount.amount.toDoubleOrNull() ?: 0.0
    }

    val currency = selectedOptions.first().estimatedCostAmount.currencyCode

    return Money("%.2f".format(totalShipping), currency)
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
        quantity = quantity,
        isAvailableForSale = isAvailableForSale,
        quantityAvailable = quantityAvailable ?: 0,
    )
}