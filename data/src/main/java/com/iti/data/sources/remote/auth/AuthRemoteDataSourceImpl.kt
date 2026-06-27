package com.iti.data.sources.remote.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iti.data.core.FirestoreCollections
import com.iti.data.dto.auth.UserDto
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.models.auth.RegistrationInfo
import kotlinx.coroutines.tasks.await

class AuthRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRemoteDataSource {

    override suspend fun login(credentials: LoginCredentials): UserDto {
        auth.signInWithEmailAndPassword(credentials.email, credentials.password).await()
        val uid = auth.currentUser?.uid ?: throw AuthException.UserNotFound
        return getUserDocument(uid)
    }

    override suspend fun register(info: RegistrationInfo): UserDto {
        val result = auth.createUserWithEmailAndPassword(info.email, info.password).await()
        val uid = result.user?.uid ?: throw AuthException.UserNotFound
        
        val userDto = UserDto(
            id = uid,
            fullName = info.fullName,
            email = info.email,
            phone = info.phone
        )
        
        firestore.collection(FirestoreCollections.USERS)
            .document(uid)
            .set(userDto)
            .await()
            
        return userDto
    }

    override suspend fun getCurrentUser(): UserDto {
        val uid = auth.currentUser?.uid ?: throw AuthException.UserNotFound
        return getUserDocument(uid)
    }

    override fun logout() {
        auth.signOut()
    }
    
    private suspend fun getUserDocument(uid: String): UserDto {
        val document = firestore.collection(FirestoreCollections.USERS)
            .document(uid)
            .get()
            .await()
            
        return document.toObject(UserDto::class.java) ?: throw AuthException.UserNotFound
    }
}
