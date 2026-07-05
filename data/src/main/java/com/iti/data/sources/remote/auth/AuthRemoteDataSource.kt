package com.iti.data.sources.remote.auth

import com.google.firebase.auth.AuthCredential
import com.iti.data.dto.auth.CredentialAuthResult
import com.iti.data.dto.auth.FirebaseUserInfo

interface AuthRemoteDataSource {
    suspend fun signInWithEmail(email: String, password: String): String
    suspend fun signInAnonymously(): String
    suspend fun createUserWithEmail(email: String, password: String): String
    suspend fun linkCurrentUserWithCredential(credential: AuthCredential): CredentialAuthResult
    suspend fun signInWithCredential(credential: AuthCredential): CredentialAuthResult

    suspend fun sendPasswordResetEmail(email: String)
    suspend fun sendEmailVerification()
    suspend fun reloadCurrentUser(): FirebaseUserInfo?
    fun getCurrentFirebaseUser(): FirebaseUserInfo?
    fun signOut()
}