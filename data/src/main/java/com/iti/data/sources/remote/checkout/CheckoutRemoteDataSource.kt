//
//  CheckoutRemoteDataSource.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.sources.remote.checkout

import com.iti.data.dto.checkout.DraftOrderDto

interface CheckoutRemoteDataSource {
    suspend fun createDraftOrder(
        lineItems: List<Pair<String, Int>>,
        street: String,
        city: String,
        country: String,
        zip: String
    ): DraftOrderDto

    suspend fun completeDraftOrder(draftOrderId: String): DraftOrderDto
}
