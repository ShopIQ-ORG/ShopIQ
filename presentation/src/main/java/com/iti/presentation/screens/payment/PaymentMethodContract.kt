//
//  PaymentMethodContract.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.payment

import com.iti.presentation.util.UiText

sealed interface PaymentMethodContract {
    data class State(
        val selectedMethod: PaymentMethodType = PaymentMethodType.COD,
        val isLoading: Boolean = false,
        val error: UiText? = null
    )

    sealed interface Intent {
        object BackClicked : Intent
        data class SelectPaymentMethod(val method: PaymentMethodType) : Intent
        object ContinueClicked : Intent
    }

    sealed interface Effect {
        object NavigateBack : Effect
        data class NavigateToNextStep(val methodType: PaymentMethodType) : Effect
    }

    enum class PaymentMethodType {
        COD, ONLINE
    }
}
