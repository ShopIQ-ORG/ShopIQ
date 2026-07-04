package com.iti.data.sources.remote.cart

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.iti.data.utils.FirebaseConstants
import kotlinx.coroutines.tasks.await

class CartIdRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : CartIdDataSource {

    override suspend fun getCartId(): String? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .get()
            .await()
            .getString(FirebaseConstants.UserFields.CART_ID)
    }

    override suspend fun saveCartId(cartId: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .set(mapOf(FirebaseConstants.UserFields.CART_ID to cartId), SetOptions.merge())
            .await()
    }

    override suspend fun clearCartId() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .update(FirebaseConstants.UserFields.CART_ID, FieldValue.delete())
            .await()
    }
}