package com.iti.data.sources.remote.favorites

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.iti.data.utils.FirebaseConstants
import kotlinx.coroutines.tasks.await

class FavoriteRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : FavoriteRemoteDataSource {

    override suspend fun getFavoriteIds(userId: String): List<String> {
        val docSnapshot = firestore.collection(FirebaseConstants.Collections.USERS)
            .document(userId)
            .get()
            .await()

        return (docSnapshot.get(FirebaseConstants.UserFields.FAVORITES) as? List<*>)
            ?.filterIsInstance<String>()
            ?: emptyList()
    }

    override suspend fun addFavorite(userId: String, productId: String) {
        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(userId)
            .set(
                mapOf(FirebaseConstants.UserFields.FAVORITES to FieldValue.arrayUnion(productId)),
                SetOptions.merge()
            )
            .await()
    }

    override suspend fun removeFavorite(userId: String, productId: String) {
        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(userId)
            .set(
                mapOf(FirebaseConstants.UserFields.FAVORITES to FieldValue.arrayRemove(productId)),
                SetOptions.merge()
            )
            .await()
    }
}