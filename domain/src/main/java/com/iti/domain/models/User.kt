//
//  User.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.models

sealed class User {
    data class AuthenticatedUser(
        val uid: String,
        val fullName: String,
        val email: String,
        val phone: String,
        val dateOfBirth: String? = null,
        val gender: String? = null,
        val avatarUrl: String? = null
    ) : User()

    object GuestUser : User()
}