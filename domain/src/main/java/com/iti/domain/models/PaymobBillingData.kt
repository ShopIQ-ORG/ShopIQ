package com.iti.domain.models

data class PaymobBillingData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val street: String,
    val city: String = "Cairo"
)