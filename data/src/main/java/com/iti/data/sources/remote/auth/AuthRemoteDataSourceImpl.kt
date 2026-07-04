package com.iti.data.sources.remote.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.iti.data.dto.auth.CredentialAuthResult
import com.iti.data.dto.auth.FirebaseUserInfo
import com.iti.domain.exceptions.AuthException
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSourceImpl(
    private val auth: FirebaseAuth
) : AuthRemoteDataSource {

    override suspend fun signInWithEmail(email: String, password: String): String {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser?.uid ?: throw AuthException.UserNotFound()
    }

    override suspend fun signInAnonymously(): String {
        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: throw AuthException.UserNotFound()
    }

    override suspend fun createUserWithEmail(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw AuthException.UserNotFound()
    }

    override suspend fun linkCurrentUserWithCredential(credential: AuthCredential): CredentialAuthResult {
        val currentUser = auth.currentUser ?: throw AuthException.UserNotFound()
        return currentUser.linkWithCredential(credential).await().toCredentialAuthResult()
    }

    override suspend fun signInWithCredential(credential: AuthCredential): CredentialAuthResult {
        return auth.signInWithCredential(credential).await().toCredentialAuthResult()
    }

    override fun getCurrentFirebaseUser(): FirebaseUserInfo? {
        val user = auth.currentUser ?: return null
        return FirebaseUserInfo(
            uid = user.uid,
            isAnonymous = user.isAnonymous,
            displayName = user.displayName,
            email = user.email
        )
    }

    override fun signOut() = auth.signOut()

    private fun AuthResult.toCredentialAuthResult(): CredentialAuthResult {
        val uid = user?.uid ?: throw AuthException.UserNotFound()
        val profile = additionalUserInfo?.profile
        return CredentialAuthResult(
            uid = uid,
            fullName = (profile?.get("name") as? String).orEmpty(),
            email = (profile?.get("email") as? String).orEmpty()
        )
    }
}