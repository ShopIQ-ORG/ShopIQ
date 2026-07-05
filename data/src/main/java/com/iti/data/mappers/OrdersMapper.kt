package com.iti.data.mappers

import com.iti.data.dto.orders.MoneyDto
import com.iti.data.dto.orders.OrderDto
import com.iti.data.dto.orders.OrderLineItemDto
import com.iti.data.dto.orders.OrderLineItemVariantDto
import com.iti.data.dto.orders.ShippingAddressDto
import com.iti.data.storefront.GetCustomerOrdersQuery
import com.iti.data.storefront.fragment.MoneyFields
import com.iti.domain.models.order.Money
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.OrderFinancialStatus
import com.iti.domain.models.order.OrderLineItem
import com.iti.domain.models.order.OrderLineItemVariant
import com.iti.domain.models.order.OrderStatus
import com.iti.domain.models.order.ShippingAddress

fun OrderDto.toDomain(): Order = Order(
    id = id,
    name = name,
    createdAt = processedAt,
    financialStatus = financialStatus.toFinancialStatus(),
    fulfillmentStatus = fulfillmentStatus.toOrderStatus(),
    subtotalPrice = subtotalPrice.toDomain(),
    totalShippingPrice = totalShippingPrice.toDomain(),
    totalPrice = totalPrice.toDomain(),
    totalRefunded = totalRefunded.toDomain(),
    totalTax = totalTax.toDomain(),
    shippingAddress = shippingAddress?.let {
        ShippingAddress(
            firstName = it.firstName,
            lastName = it.lastName,
            address1 = it.address1,
            city = it.city,
            country = it.country,
            zip = it.zip
        )
    },
    lineItems = lineItems.map { it.toDomain() }
)

private fun OrderLineItemDto.toDomain(): OrderLineItem = OrderLineItem(
    title = title,
    quantity = quantity,
    currentQuantity = currentQuantity,
    originalTotalPrice = originalTotalPrice.toDomain(),
    discountedTotalPrice = discountedTotalPrice.toDomain(),
    variant = variant?.let {
        OrderLineItemVariant(
            id = it.id,
            title = it.title,
            sku = it.sku,
            price = it.price.toDomain(),
            imageUrl = it.imageUrl,
            productId = it.productId,
            productTitle = it.productTitle,
            productHandle = it.productHandle
        )
    }
)

private fun MoneyDto.toDomain(): Money = Money(amount = amount.toDoubleOrNull() ?: 0.0, currencyCode = currencyCode)

private fun String.toFinancialStatus(): OrderFinancialStatus =
    OrderFinancialStatus.entries.find { it.name == this.uppercase() } ?: OrderFinancialStatus.UNKNOWN

private fun String.toOrderStatus(): OrderStatus = when (this.uppercase()) {
    "PENDING_FULFILLMENT", "OPEN", "UNFULFILLED" -> OrderStatus.PENDING
    "IN_PROGRESS", "PARTIALLY_FULFILLED", "ON_HOLD", "SCHEDULED" -> OrderStatus.PROCESSING
    "FULFILLED" -> OrderStatus.COMPLETED
    "RESTOCKED" -> OrderStatus.CANCELLED
    else -> OrderStatus.UNKNOWN
}


fun GetCustomerOrdersQuery.Node.toDto(): OrderDto = OrderDto(
    id = id,
    name = name,
    processedAt = processedAt.toString(),
    financialStatus = financialStatus.toString(),
    fulfillmentStatus = fulfillmentStatus.toString(),
    subtotalPrice = subtotalPrice.moneyFields.toDto(),
    totalShippingPrice = totalShippingPrice.moneyFields.toDto(),
    totalPrice = totalPrice.moneyFields.toDto(),
    totalRefunded = totalRefunded.moneyFields.toDto(),
    totalTax = totalTax.moneyFields.toDto(),
    shippingAddress = shippingAddress?.let {
        ShippingAddressDto(
            firstName = it.firstName,
            lastName = it.lastName,
            address1 = it.address1,
            city = it.city,
            country = it.country,
            zip = it.zip
        )
    },
    lineItems = lineItems.nodes.map { it.toDto() }
)

private fun GetCustomerOrdersQuery.Node1.toDto(): OrderLineItemDto = OrderLineItemDto(
    title = title,
    quantity = quantity,
    currentQuantity = currentQuantity,
    originalTotalPrice = originalTotalPrice.moneyFields.toDto(),
    discountedTotalPrice = discountedTotalPrice.moneyFields.toDto(),
    variant = variant?.let {
        OrderLineItemVariantDto(
            id = it.id,
            title = it.title,
            sku = it.sku,
            price = it.price.moneyFields.toDto(),
            imageUrl = it.image?.url?.toString(),
            productId = it.product.id,
            productTitle = it.product.title,
            productHandle = it.product.handle
        )
    }
)

private fun MoneyFields.toDto(): MoneyDto = MoneyDto(amount = amount, currencyCode = currencyCode.toString())
