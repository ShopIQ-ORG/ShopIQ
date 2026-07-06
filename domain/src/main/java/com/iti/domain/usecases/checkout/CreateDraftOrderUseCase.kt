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
import com.iti.domain.models.cart.Cart
import com.iti.domain.repositories.checkout.CheckoutRepository

class CreateDraftOrderUseCase(
    private val repository: CheckoutRepository
) {
    suspend operator fun invoke(
        cart: Cart,
        shippingAddress: Address?,
        email: String?
    ): Result<DraftOrder> {
        return repository.createDraftOrder(cart, shippingAddress, email)
    }
}
