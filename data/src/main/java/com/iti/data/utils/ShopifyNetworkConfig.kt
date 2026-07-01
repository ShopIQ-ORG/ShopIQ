package com.iti.data.utils

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.iti.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

object ShopifyNetworkConfig {

    private const val API_VERSION = "2024-07"
    private const val BASE_URL = "https://${BuildConfig.SHOPIFY_STORE_DOMAIN}/admin/api/$API_VERSION/graphql.json"
    private const val STOREFRONT_BASE_URL = "https://${BuildConfig.SHOPIFY_STORE_DOMAIN}/api/$API_VERSION/graphql.json"

    private const val ADMIN_ACCESS_TOKEN = BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN
    private const val STOREFRONT_ACCESS_TOKEN = BuildConfig.SHOPIFY_STOREFRONT_ACCESS_TOKEN

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AdminInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val storefrontOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(StorefrontInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    val storefrontApolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(STOREFRONT_BASE_URL)
            .okHttpClient(storefrontOkHttpClient)
            .build()
    }

    private class AdminInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Access-Token", ADMIN_ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build()
            return chain.proceed(request)
        }
    }

    private class StorefrontInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("X-Shopify-Storefront-Access-Token", STOREFRONT_ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build()
            return chain.proceed(request)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}
