//
//  CreateDraftOrderUseCase.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.checkout

import com.iti.domain.models.Result
import com.iti.domain.models.checkout.DraftOrder
import com.iti.domain.models.Address
import com.iti.domain.repositories.checkout.CheckoutRepository

class CreateDraftOrderUseCase(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(
        lineItems: List<Pair<String, Int>>,
        shippingAddress: Address?
    ): Result<DraftOrder> {
        return repository.createDraftOrder(lineItems, shippingAddress)
    }
}
