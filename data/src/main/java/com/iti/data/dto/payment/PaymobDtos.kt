package com.iti.data.dto.payment

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AuthRequest(
    @SerialName("api_key") val apiKey: String
)

@Serializable
data class AuthResponse(
    @SerialName("token") val token: String
)

@Serializable
data class OrderRequest(
    @SerialName("auth_token") val authToken: String,
    @SerialName("delivery_needed") val deliveryNeeded: Boolean = false,
    @SerialName("amount_cents") val amountCents: Long,
    @SerialName("currency") val currency: String = "EGP"
)

@Serializable
data class OrderResponse(
    @SerialName("id") val id: Long
)

@Serializable
data class PaymentKeyRequest(
    @SerialName("auth_token") val authToken: String,
    @SerialName("amount_cents") val amountCents: Long,
    @SerialName("expiration") val expiration: Int = 3600,
    @SerialName("order_id") val orderId: String,
    @SerialName("billing_data") val billingData: SandboxBillingData,
    @SerialName("currency") val currency: String = "EGP",
    @SerialName("integration_id") val integrationId: Int
)

@Serializable
data class SandboxBillingData(
    @SerialName("first_name") val firstName: String = "Sherif",
    @SerialName("last_name") val lastName: String = "Ashraf",
    @SerialName("email") val email: String = "clannad@iti.com",
    @SerialName("phone_number") val phoneNumber: String = "+201000000000",
    @SerialName("apartment") val apartment: String = "NA",
    @SerialName("floor") val floor: String = "NA",
    @SerialName("street") val street: String = "Smart Village",
    @SerialName("building") val building: String = "NA",
    @SerialName("shipping_method") val shippingMethod: String = "PKG",
    @SerialName("postal_code") val postalCode: String = "NA",
    @SerialName("city") val city: String = "Cairo",
    @SerialName("state") val state: String = "Cairo",
    @SerialName("country") val country: String = "EG"
)

@Serializable
data class PaymentKeyResponse(
    @SerialName("token") val token: String
)
