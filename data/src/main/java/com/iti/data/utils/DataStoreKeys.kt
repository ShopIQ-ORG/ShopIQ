package com.iti.data.utils
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {

    val SHOPIFY_CUSTOMER_ID = stringPreferencesKey("shopify_customer_id")
    val SHOPIFY_ACCESS_TOKEN = stringPreferencesKey("shopify_access_token")
    val SHOPIFY_EXPIRES_AT = stringPreferencesKey("shopify_expires_at")
    val SHOPIFY_PASSWORD = stringPreferencesKey("shopify_password")
}