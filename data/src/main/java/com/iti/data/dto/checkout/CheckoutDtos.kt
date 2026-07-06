//
//  CheckoutDtos.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.dto.checkout

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>
)

data class DraftOrderInput(
    val customerId: String?,
    val email: String?,
    val lineItems: List<DraftOrderLineItemInput>,
    val shippingAddress: MailingAddressInput?,
    val billingAddress: MailingAddressInput?,
    val appliedDiscount: AppliedDiscountInput?,
    val shippingLine: ShippingLineInput?,
    val note: String?,
    val customAttributes: List<CustomAttributeInput>?,
    val tags: List<String>?,
    val taxExempt: Boolean?,
    val useCustomerDefaultAddress: Boolean?
)

data class DraftOrderLineItemInput(
    val variantId: String,
    val quantity: Int
)

data class MailingAddressInput(
    val firstName: String?,
    val lastName: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val province: String?,
    val country: String?,
    val zip: String?,
    val phone: String?
)

data class AppliedDiscountInput(
    val title: String?,
    val description: String?,
    val value: Double,
    val valueType: String
)

data class ShippingLineInput(
    val title: String,
    val priceWithCurrency: MoneyInput
)

data class MoneyInput(
    val amount: Double,
    val currencyCode: String
)

data class CustomAttributeInput(
    val key: String,
    val value: String
)

data class GraphQLResponse<T>(
    val data: T?,
    val errors: List<GraphQLErrorDto>?
)

data class GraphQLErrorDto(
    val message: String
)

data class DraftOrderCreateData(
    val draftOrderCreate: DraftOrderCreatePayload?
)

data class DraftOrderCreatePayload(
    val draftOrder: DraftOrderDto?,
    val userErrors: List<UserErrorDto>?
)

data class DraftOrderCompleteData(
    val draftOrderComplete: DraftOrderCompletePayload?
)

data class DraftOrderCompletePayload(
    val draftOrder: DraftOrderDto?,
    val userErrors: List<UserErrorDto>?
)

data class DraftOrderDto(
    val id: String,
    val totalPrice: String?,
    val subtotalPrice: String?,
    val totalTax: String?,
    val status: String?,
    val order: OrderDto? = null
)

data class OrderDto(
    val id: String,
    val name: String
)

data class UserErrorDto(
    val field: List<String>?,
    val message: String
)
