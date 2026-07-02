package com.iti.data.sources.local.shopify

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import kotlinx.coroutines.flow.first

private val KEY_CUSTOMER_ID = stringPreferencesKey("shopify_customer_id")
private val KEY_ACCESS_TOKEN = stringPreferencesKey("shopify_access_token")
private val KEY_EXPIRES_AT = stringPreferencesKey("shopify_expires_at")
private val KEY_PASSWORD = stringPreferencesKey("shopify_password")

class ShopifyTokenLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : ShopifyTokenLocalDataSource {

    override suspend fun getCachedFields(): ShopifyFieldsDto? {
        val prefs = dataStore.data.first()
        val accessToken = prefs[KEY_ACCESS_TOKEN] ?: return null
        val expiresAt = prefs[KEY_EXPIRES_AT] ?: return null
        return ShopifyFieldsDto(
            customerId = prefs[KEY_CUSTOMER_ID],
            accessToken = accessToken,
            expiresAt = expiresAt,
            password = prefs[KEY_PASSWORD]
        )
    }

    override suspend fun saveFields(fields: ShopifyFieldsDto) {
        dataStore.edit { prefs ->
            fields.customerId?.let { prefs[KEY_CUSTOMER_ID] = it }
            prefs[KEY_ACCESS_TOKEN] = fields.accessToken
            prefs[KEY_EXPIRES_AT] = fields.expiresAt
            fields.password?.let { prefs[KEY_PASSWORD] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}