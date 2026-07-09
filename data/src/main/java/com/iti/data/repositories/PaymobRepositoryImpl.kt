package com.iti.data.repositories

import com.iti.data.sources.remote.payment.PaymobRemoteDataSource
import com.iti.domain.exceptions.AppException
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
        return try {
            val intentionResponse = remoteDataSource.createIntention(
                secretKey = secretKey,
                amountCents = amountCents,
                currency = currency,
                integrationId = integrationId
            )

            Result.success(
                PaymobIntentionResult(
                    clientSecret = intentionResponse.client_secret,
                    publicKey = publicKey
                )
            )
        } catch (e: Exception) {
            Result.failure(AppException.PaymentIntentionCreationFailed())
        }
    }
}
