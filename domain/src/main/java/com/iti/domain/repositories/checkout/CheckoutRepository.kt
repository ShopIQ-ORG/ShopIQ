//
//  CheckoutRepository.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.repositories.checkout

import com.iti.domain.models.Result
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart

interface CheckoutRepository {
    suspend fun createDraftOrder(
        cart: Cart,
        shippingAddress: Address?,
        email: String?
    ): Result<DraftOrder>

    suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder>
}
