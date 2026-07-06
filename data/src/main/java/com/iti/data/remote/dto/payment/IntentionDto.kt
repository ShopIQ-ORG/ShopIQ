package com.iti.data.remote.dto.payment

import kotlinx.serialization.Serializable

@Serializable
data class IntentionRequest(
    val amount: Long,
    val currency: String,
    val payment_methods: List<Int>,
    val billing_data: BillingDataDto
)

@Serializable
data class BillingDataDto(
    val first_name: String,
    val last_name: String,
    val phone_number: String,
    val email: String,
    val apartment: String = "dummy",
    val floor: String = "dummy",
    val street: String = "dummy",
    val building: String = "dummy",
    val city: String = "dummy",
    val country: String = "dummy",
    val state: String = "dummy"
)

@Serializable
data class IntentionResponse(
    val client_secret: String,
    val id: String
)