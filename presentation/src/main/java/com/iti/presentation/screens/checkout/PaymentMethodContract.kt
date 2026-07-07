<<<<<<<< HEAD:presentation/src/main/java/com/iti/presentation/screens/payment/PaymentMethodContract.kt
//
//  PaymentMethodContract.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.payment
========
package com.iti.presentation.screens.checkout
>>>>>>>> feature/paymob-payment:presentation/src/main/java/com/iti/presentation/screens/checkout/PaymentMethodContract.kt

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
        data class NavigateToNextStep(val methodType: PaymentMethodType, val amountCents: Long) : Effect
        data class ShowCodLimitError(val message: String) : Effect
    }

    enum class PaymentMethodType {
        COD, ONLINE
    }
}
