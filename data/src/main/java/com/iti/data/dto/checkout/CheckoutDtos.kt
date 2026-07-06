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
