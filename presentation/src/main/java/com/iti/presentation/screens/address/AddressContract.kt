//
//  AddressContract.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address

import com.iti.domain.models.Address
import com.iti.presentation.util.UiText

object AddressContract {

    sealed class ScreenState {
        data object Loading : ScreenState()
        data object Empty : ScreenState()
        data object GPSOnboarding : ScreenState()
        data class LocationDetected(val address: Address) : ScreenState()
        data class Success(val addresses: List<Address>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class State(
        val screenState: ScreenState = ScreenState.Loading,
        val addresses: List<Address> = emptyList(),
        val showSuccessBadge: Boolean = false,
        val triggerPermissionRequest: Boolean = false,
        val isDetectingLocation: Boolean = false
    )

    sealed class Intent {
        data object LoadAddresses : Intent()
        data object AddAddressClicked : Intent()
        data object RequestGPSLocation : Intent()
        data object PermissionGranted : Intent()
        data object PermissionDenied : Intent()
        data class ConfirmAddress(val name: String, val isDefault: Boolean) : Intent()
        data class DeleteAddress(val addressId: String) : Intent()
        data class SetDefaultAddress(val addressId: String) : Intent()
        data object DismissSuccessBadge : Intent()
        data object CancelAddAddress : Intent()
        data object NavigateBack : Intent()
    }

    sealed class Effect {
        data object NavigateBack : Effect()
        data class ShowMessage(val message: UiText) : Effect()
    }
}
