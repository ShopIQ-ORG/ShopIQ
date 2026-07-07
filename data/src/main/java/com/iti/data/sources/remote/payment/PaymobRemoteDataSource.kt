package com.iti.data.sources.remote.payment

import com.iti.data.remote.dto.payment.BillingDataDto
import com.iti.data.remote.dto.payment.IntentionRequest
import com.iti.data.remote.dto.payment.IntentionResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.client.statement.bodyAsText

class PaymobRemoteDataSource(private val client: HttpClient) {

    suspend fun createIntention(
        secretKey: String,
        amountCents: Long,
        currency: String,
        integrationId: Int
    ): IntentionResponse {
        val response = client.post("https://accept.paymob.com/v1/intention/") {
            header("Authorization", "Token $secretKey")
            setBody(
                IntentionRequest(
                    amount = amountCents,
                    currency = currency,
                    payment_methods = listOf(integrationId),
                    billing_data = BillingDataDto(
                        first_name = "NA",
                        last_name = "NA",
                        phone_number = "+201000000000",
                        email = "test@test.com"
                    )
                )
            )
        }
        if (response.status.value !in 200..299) {
            val errorBody = response.bodyAsText()
            throw Exception("Intention API Error: ${response.status.value} - $errorBody")
        }
        return response.body()
    }
}
