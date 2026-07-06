package com.iti.data.sources.local.shopify

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.data.utils.DataStoreKeys
import kotlinx.coroutines.flow.first

class ShopifyTokenLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : ShopifyTokenLocalDataSource {

    override suspend fun getCachedFields(): ShopifyFieldsDto? {
        val prefs = dataStore.data.first()
        val accessToken = prefs[DataStoreKeys.SHOPIFY_ACCESS_TOKEN] ?: return null
        val expiresAt = prefs[DataStoreKeys.SHOPIFY_EXPIRES_AT] ?: return null
        return ShopifyFieldsDto(
            customerId = prefs[DataStoreKeys.SHOPIFY_CUSTOMER_ID],
            accessToken = accessToken,
            expiresAt = expiresAt,
            password = prefs[DataStoreKeys.SHOPIFY_PASSWORD]
        )
    }

    override suspend fun saveFields(fields: ShopifyFieldsDto) {
        dataStore.edit { prefs ->
            fields.customerId?.let { prefs[DataStoreKeys.SHOPIFY_CUSTOMER_ID] = it }
            prefs[DataStoreKeys.SHOPIFY_ACCESS_TOKEN] = fields.accessToken
            prefs[DataStoreKeys.SHOPIFY_EXPIRES_AT] = fields.expiresAt
            fields.password?.let { prefs[DataStoreKeys.SHOPIFY_PASSWORD] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(DataStoreKeys.SHOPIFY_CUSTOMER_ID)
            prefs.remove(DataStoreKeys.SHOPIFY_ACCESS_TOKEN)
            prefs.remove(DataStoreKeys.SHOPIFY_EXPIRES_AT)
            prefs.remove(DataStoreKeys.SHOPIFY_PASSWORD)
        }
    }
}