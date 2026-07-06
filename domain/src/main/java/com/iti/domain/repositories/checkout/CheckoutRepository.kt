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

interface CheckoutRepository {
    suspend fun createDraftOrder(
        lineItems: List<Pair<String, Int>>,
        shippingAddress: Address?
    ): Result<DraftOrder>

    suspend fun completeDraftOrder(draftOrderId: String): Result<DraftOrder>
}
