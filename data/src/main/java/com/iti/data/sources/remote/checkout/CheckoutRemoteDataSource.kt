//
//  CheckoutRemoteDataSource.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.sources.remote.checkout

import com.iti.data.dto.checkout.DraftOrderDto
import com.iti.data.dto.checkout.DraftOrderInput

interface CheckoutRemoteDataSource {
    suspend fun createDraftOrder(
        input: DraftOrderInput
    ): DraftOrderDto

    suspend fun completeDraftOrder(draftOrderId: String): DraftOrderDto
}
