//
//  DraftOrder.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.models.checkout

data class DraftOrder(
    val id: String,
    val totalPrice: String,
    val subtotalPrice: String,
    val totalTax: String,
    val status: String? = null,
    val orderNumber: String? = null
)
