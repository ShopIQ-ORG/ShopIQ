package com.iti.domain.repositories.payment

interface PaymobRepository {
    suspend fun createPaymentIntention(
        amountCents: Long,
        currency: String,
        integrationId: Int
    ): Result<PaymobIntentionResult>
}

data class PaymobIntentionResult(
    val clientSecret: String,
    val publicKey: String
)