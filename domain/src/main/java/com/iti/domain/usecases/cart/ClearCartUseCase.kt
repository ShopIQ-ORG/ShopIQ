//
//  ClearCartUseCase.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.cart

import com.iti.domain.models.Result
import com.iti.domain.repositories.cart.CartRepository

class ClearCartUseCase(
    private val repository: CartRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearCart()
    }
}
