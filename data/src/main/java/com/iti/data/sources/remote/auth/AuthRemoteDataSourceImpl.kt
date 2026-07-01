package com.iti.data.sources.remote.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
        val previousUid = auth.currentUser?.takeIf { it.isAnonymous }?.uid

        auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
        val uid = auth.currentUser?.uid ?: throw AuthException.UserNotFound()

        if (previousUid != null && previousUid != uid) {
            mergeGuestCartInto(previousUid, uid)
        }

        return getUserDocument(uid)
    }

    override suspend fun loginWithGoogle(idToken: String): UserDto {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return linkOrSignIn(credential) { uid, profile ->
            UserDto(
                id = uid,
                fullName = (profile?.get("name") as? String).orEmpty(),
                email = (profile?.get("email") as? String).orEmpty()
            )
        }
    }

    override suspend fun loginWithFacebook(accessToken: String): UserDto {
        val credential = FacebookAuthProvider.getCredential(accessToken)
        return linkOrSignIn(credential) { uid, profile ->
            UserDto(
                id = uid,
                fullName = (profile?.get("name") as? String).orEmpty(),
                email = (profile?.get("email") as? String).orEmpty()
            )
        }
    }

    override suspend fun loginAsGuest(): UserDto {
        val result = auth.signInAnonymously().await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound()
        val userDto = UserDto(id = uid, isGuest = true)
        saveUserDocument(uid, userDto, merge = true)
        return userDto
    }

    override suspend fun register(info: RegistrationInfo): UserDto {
        val currentUser = auth.currentUser
        val userDto: UserDto
        val uid: String

        if (currentUser != null && currentUser.isAnonymous) {
            val credential = EmailAuthProvider.getCredential(info.email, info.password)
            try {
                val result = currentUser.linkWithCredential(credential).await()
                uid = result.user?.uid ?: throw AuthException.UserNotFound()
                userDto = info.toUserDto(uid).copy(isGuest = false)
                saveUserDocument(uid, userDto, merge = true)
                return userDto
            } catch (_: FirebaseAuthUserCollisionException) {
                throw AuthException.EmailAlreadyInUse()
            }
        }

        val result = auth.createUserWithEmailAndPassword(info.email, info.password).await()
        uid = result.user?.uid ?: throw AuthException.UserNotFound()
        userDto = info.toUserDto(uid)
        saveUserDocument(uid, userDto, merge = false)
        return userDto
    }

    override suspend fun getCurrentUser(): UserDto {
        val firebaseUser = auth.currentUser ?: throw AuthException.UserNotFound()
        if (firebaseUser.isAnonymous) return UserDto(id = firebaseUser.uid, isGuest = true)
        return try {
            getUserDocument(firebaseUser.uid)
        } catch (e: Exception) {
            // Fallback to basic Firebase info if Firestore document is missing or error occurs
            UserDto(
                id = firebaseUser.uid,
                fullName = firebaseUser.displayName.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                isGuest = false
            )
        }
    }

    override fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun logout() = auth.signOut()

    private suspend fun linkOrSignIn(
        credential: AuthCredential,
        buildUserDto: (uid: String, profile: Map<String, Any?>?) -> UserDto
    ): UserDto {
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isAnonymous) {
            try {
                val result = currentUser.linkWithCredential(credential).await()
                val uid = result.user?.uid ?: throw AuthException.UserNotFound()
                val dto = buildUserDto(uid, result.additionalUserInfo?.profile)
                saveUserDocument(uid, dto, merge = true)
                return dto
            } catch (_: FirebaseAuthUserCollisionException) {
                val previousUid = currentUser.uid
                val result = auth.signInWithCredential(credential).await()
                val uid = result.user?.uid ?: throw AuthException.UserNotFound()
                mergeGuestCartInto(previousUid, uid)
                return getUserDocument(uid)
            }
        }

        val result = auth.signInWithCredential(credential).await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound()
        val dto = buildUserDto(uid, result.additionalUserInfo?.profile)
        saveUserDocument(uid, dto, merge = true)
        return dto
    }

    private suspend fun mergeGuestCartInto(guestUid: String, targetUid: String) {
        val guestSnapshot = firestore.collection(FirebaseConstants.Collections.USERS)
            .document(guestUid)
            .get()
            .await()

        val cartId = guestSnapshot.getString(FirebaseConstants.UserFields.CART_ID) ?: return

        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(targetUid)
            .set(mapOf(FirebaseConstants.UserFields.CART_ID to cartId), SetOptions.merge())
            .await()

        firestore.collection(FirebaseConstants.Collections.USERS)
            .document(guestUid)
            .delete()
            .await()
    }

    private suspend fun getUserDocument(uid: String): UserDto {
        return firestore.collection(FirebaseConstants.Collections.USERS)
            .document(uid)
            .get()
            .await()
            .toObject(UserDto::class.java) ?: throw AuthException.UserNotFound()
    }

    private suspend fun saveUserDocument(uid: String, userDto: UserDto, merge: Boolean) {
        val ref = firestore.collection(FirebaseConstants.Collections.USERS).document(uid)
        if (merge) ref.set(userDto, SetOptions.merge()).await() else ref.set(userDto).await()
    }
}