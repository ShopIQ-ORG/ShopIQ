package com.iti.data.sources.remote.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.iti.data.core.FirebaseConstants
import com.iti.data.dto.auth.UserDto
import com.iti.data.dto.shopifycustomer.ShopifyFieldsDto
import com.iti.domain.exceptions.AuthException
import kotlinx.coroutines.tasks.await

class UserRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    private val usersCollection get() =
        firestore.collection(FirebaseConstants.Collections.USERS)

    override suspend fun getUser(uid: String): UserDto {
        return getUserOrNull(uid) ?: throw AuthException.UserNotFound()
    }

    override suspend fun getUserOrNull(uid: String): UserDto? {
        return usersCollection.document(uid).get().await().toObject(UserDto::class.java)
    }

    override suspend fun saveUser(uid: String, user: UserDto, merge: Boolean) {
        val ref = usersCollection.document(uid)
        if (merge) ref.set(user, SetOptions.merge()).await() else ref.set(user).await()
    }

    override suspend fun updateShopifyFields(uid: String, fields: ShopifyFieldsDto) {
        val updates = mutableMapOf<String, Any?>(
            FirebaseConstants.UserFields.SHOPIFY_CUSTOMER_ID to fields.customerId,
            FirebaseConstants.UserFields.SHOPIFY_ACCESS_TOKEN to fields.accessToken,
            FirebaseConstants.UserFields.SHOPIFY_TOKEN_EXPIRES_AT to fields.expiresAt
        )
        if (fields.password != null) {
            updates[FirebaseConstants.UserFields.SHOPIFY_PASSWORD] = fields.password
        }
        usersCollection.document(uid).set(updates, SetOptions.merge()).await()
    }
}