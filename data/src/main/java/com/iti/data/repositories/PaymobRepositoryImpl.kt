package com.iti.data.repositories

import com.iti.data.sources.remote.payment.PaymobRemoteDataSource
import com.iti.domain.repositories.payment.PaymobIntentionResult
import com.iti.domain.repositories.payment.PaymobRepository

class PaymobRepositoryImpl(
    private val remoteDataSource: PaymobRemoteDataSource,
    private val secretKey: String,
    private val publicKey: String
) : PaymobRepository {

    override suspend fun createPaymentIntention(
        amountCents: Long,
        currency: String,
        integrationId: Int
    ): Result<PaymobIntentionResult> {
        return runCatching {
            // Single-step: Create Payment Intention using the new API
            val intentionResponse = remoteDataSource.createIntention(
                secretKey = secretKey,
                amountCents = amountCents,
                currency = currency,
                integrationId = integrationId
            )

            PaymobIntentionResult(
                clientSecret = intentionResponse.client_secret,
                publicKey = publicKey
            )
        }
    }
}
