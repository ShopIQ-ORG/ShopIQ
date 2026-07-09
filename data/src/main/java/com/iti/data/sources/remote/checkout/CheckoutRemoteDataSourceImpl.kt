//
//  CheckoutRemoteDataSourceImpl.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.sources.remote.checkout

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iti.data.BuildConfig
import com.iti.data.dto.checkout.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CheckoutRemoteDataSourceImpl(
    private val gson: Gson
) : CheckoutRemoteDataSource {

    private val client = OkHttpClient.Builder().build()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val apiUrl = "https://${BuildConfig.SHOPIFY_STORE_DOMAIN}/admin/api/2024-07/graphql.json"

    override suspend fun createDraftOrder(
        input: DraftOrderInput
    ): DraftOrderDto {
        val mutation = """
            mutation draftOrderCreate(${'$'}input: DraftOrderInput!) {
              draftOrderCreate(input: ${'$'}input) {
                draftOrder {
                  id
                  totalPrice
                  subtotalPrice
                  totalTax
                  status
                }
                userErrors {
                  field
                  message
                }
              }
            }
        """.trimIndent()

        val variables = mapOf("input" to input)

        val requestPayload = GraphQLRequest(query = mutation, variables = variables)
        val requestBodyJson = gson.toJson(requestPayload)

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBodyJson.toRequestBody(jsonMediaType))
            .addHeader("X-Shopify-Access-Token", BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected HTTP code: ${response.code}")
            }
            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            
            val responseType = object : TypeToken<GraphQLResponse<DraftOrderCreateData>>() {}.type
            val graphQLResponse: GraphQLResponse<DraftOrderCreateData> = gson.fromJson(responseBody, responseType)

            if (!graphQLResponse.errors.isNullOrEmpty()) {
                throw IOException(graphQLResponse.errors.joinToString { it.message })
            }

            val payload = graphQLResponse.data?.draftOrderCreate
                ?: throw IOException("GraphQL response data is null")

            if (!payload.userErrors.isNullOrEmpty()) {
                throw IOException(payload.userErrors.joinToString { it.message })
            }

            return payload.draftOrder ?: throw IOException("Draft order not created")
        }
    }

    override suspend fun completeDraftOrder(draftOrderId: String): DraftOrderDto {
        val mutation = $$"""
            mutation draftOrderComplete($id: ID!) {
              draftOrderComplete(id: $id) {
                draftOrder {
                  id
                  totalPrice
                  subtotalPrice
                  totalTax
                  status
                  order {
                    id
                    name
                  }
                }
                userErrors {
                  field
                  message
                }
              }
            }
        """.trimIndent()

        val variables = mapOf("id" to draftOrderId)
        val requestPayload = GraphQLRequest(query = mutation, variables = variables)
        val requestBodyJson = gson.toJson(requestPayload)

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBodyJson.toRequestBody(jsonMediaType))
            .addHeader("X-Shopify-Access-Token", BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected HTTP code: ${response.code}")
            }
            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            
            val responseType = object : TypeToken<GraphQLResponse<DraftOrderCompleteData>>() {}.type
            val graphQLResponse: GraphQLResponse<DraftOrderCompleteData> = gson.fromJson(responseBody, responseType)

            if (!graphQLResponse.errors.isNullOrEmpty()) {
                throw IOException(graphQLResponse.errors.joinToString { it.message })
            }

            val payload = graphQLResponse.data?.draftOrderComplete
                ?: throw IOException("GraphQL response data is null")

            if (!payload.userErrors.isNullOrEmpty()) {
                throw IOException(payload.userErrors.joinToString { it.message })
            }

            return payload.draftOrder ?: throw IOException("Draft order not completed")
        }
    }
}
