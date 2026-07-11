//
//  CompleteDraftOrderUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.checkout

import com.iti.domain.models.Result
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.repositories.order.OrderRepository

class CompleteDraftOrderUseCase(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(draftOrderId: String): Result<DraftOrder> {
        return repository.completeDraftOrder(draftOrderId)
    }
}
