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
        data class LocationDetected(val address: Address, val isFromGps: Boolean) : ScreenState()
        data class MapPicker(val initialLatitude: Double, val initialLongitude: Double) : ScreenState()
        data class Success(val addresses: List<Address>) : ScreenState()
        data class Failure(val message: UiText) : ScreenState()
    }

    data class PlaceSuggestion(
        val displayName: String,
        val latitude: Double,
        val longitude: Double
    )

    data class State(
        val screenState: ScreenState = ScreenState.Loading,
        val addresses: List<Address> = emptyList(),
        val showSuccessBadge: Boolean = false,
        val triggerPermissionRequest: Boolean = false,
        val isDetectingLocation: Boolean = false,
        val searchSuggestions: List<PlaceSuggestion> = emptyList(),
        val errorText: UiText? = null
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
        data object OpenMapPicker : Intent()
        data class LocationSelectedFromMap(val latitude: Double, val longitude: Double) : Intent()
        data object CancelMapPicker : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data object ClearSuggestions : Intent()
        data object ClearError : Intent()
        data object NavigateBack : Intent()
    }

    sealed class Effect {
        data object NavigateBack : Effect()
        data class ShowMessage(val message: UiText) : Effect()
        data class MoveCameraToLocation(val latitude: Double, val longitude: Double) : Effect()
    }
}
