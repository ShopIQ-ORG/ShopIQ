package com.iti.data.sources.remote.auth

import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.core.FirebaseConstants
import com.iti.data.dto.auth.UserDto
import com.iti.data.mappers.toUserDto
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRemoteDataSource {

    override suspend fun login(credentials: LoginCredentials): UserDto {
        auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
        val uid = auth.currentUser?.uid ?: throw AuthException.UserNotFound()
        return getUserDocument(uid)
    }

    override suspend fun loginWithGoogle(idToken: String): UserDto {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound()
        val isNew = result.additionalUserInfo?.isNewUser == true
        if (isNew) {
            val userDto = UserDto(
                id = uid,
                fullName = result.user?.displayName.orEmpty(),
                email = result.user?.email.orEmpty()
            )
            saveUserDocument(uid, userDto)
            return userDto
        }
        return getUserDocument(uid)
    }

    override suspend fun loginWithFacebook(accessToken: String): UserDto {
        val credential = FacebookAuthProvider.getCredential(accessToken)
        val result = auth.signInWithCredential(credential).await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound()
        val isNew = result.additionalUserInfo?.isNewUser == true
        if (isNew) {
            val userDto = UserDto(
                id = uid,
                fullName = result.user?.displayName.orEmpty(),
                email = result.user?.email.orEmpty()
            )
            saveUserDocument(uid, userDto)
            return userDto
        }
        return getUserDocument(uid)
    }

    override suspend fun loginAsGuest(): UserDto {
        auth.signInAnonymously().await()
        return UserDto(isGuest = true)
    }

    override suspend fun register(info: RegistrationInfo): UserDto {
        val result = auth.createUserWithEmailAndPassword(info.email, info.password).await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound()
        val userDto = info.toUserDto(uid)
        saveUserDocument(uid, userDto)
        return userDto
    }

    override suspend fun getCurrentUser(): UserDto {
        val firebaseUser = auth.currentUser ?: throw AuthException.UserNotFound()
        if (firebaseUser.isAnonymous) return UserDto(isGuest = true)
        return getUserDocument(firebaseUser.uid)
    }

    override fun logout() = auth.signOut()

    private suspend fun getUserDocument(uid: String): UserDto {
        return firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .get()
            .await()
            .toObject(UserDto::class.java) ?: throw AuthException.UserNotFound()
    }

    private suspend fun saveUserDocument(uid: String, userDto: UserDto) {
        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .set(userDto)
            .await()
    }
}