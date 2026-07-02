package com.iti.data.dto.auth

data class FirebaseUserInfo(
    val uid: String,
    val isAnonymous: Boolean,
    val displayName: String?,
    val email: String?
)

data class CredentialAuthResult(
    val uid: String,
    val fullName: String,
    val email: String
)