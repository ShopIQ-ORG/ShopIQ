//
//  ProfileContract.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import com.iti.domain.models.Address
import com.iti.domain.models.User
import com.iti.domain.models.Currency

object ProfileContract {

    data class PlaceSuggestion(
        val displayName: String,
        val latitude: Double,
        val longitude: Double
    )

    data class State(
        val user: User? = null,
        val isUpdatingProfile: Boolean = false,
        val selectedCurrency: Currency = Currency("EGP", "Egyptian Pound", "EGP", 48.0),
        val popularCurrencies: List<Currency> = emptyList(),
        val exchangeRateLoading: Boolean = false,
        val exchangeRateHistory: List<Pair<String, Double>> = emptyList(),
        val exchangeRatesLastUpdated: String = "",
        val addresses: List<Address> = emptyList(),
        val addressLoading: Boolean = false,
        val searchSuggestions: List<PlaceSuggestion> = emptyList(),
        val isDetectingLocation: Boolean = false,
        val detectedAddress: Address? = null,
        val errorText: String? = null,
        val successText: String? = null,
        val triggerPermissionRequest: Boolean = false,
        val tempLatitude: Double? = null,
        val tempLongitude: Double? = null,
        val tempStreet: String = "",
        val tempCity: String = "",
        val tempCountry: String = "",
        val tempPostalCode: String = "",
        val shouldPopulateTempAddress: Boolean = false
    )

    sealed class Intent {
        data object ClearTempAddress : Intent()
        data object LoadProfile : Intent()
        data class UpdateProfile(
            val fullName: String,
            val email: String,
            val phone: String,
            val dateOfBirth: String,
            val gender: String,
            val avatarUrl: String?
        ) : Intent()
        data class ChangeCurrency(val currencyCode: String) : Intent()
        data object RefreshExchangeRates : Intent()
        data class LoadExchangeRateHistory(val currencyCode: String) : Intent()
        data object LoadAddresses : Intent()
        data class DeleteAddress(val addressId: String) : Intent()
        data class SetDefaultAddress(val addressId: String) : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class SelectPlaceSuggestion(val suggestion: PlaceSuggestion) : Intent()
        data object RequestGPSLocation : Intent()
        data object PermissionGranted : Intent()
        data object PermissionDenied : Intent()
        data class ConfirmAddress(
            val street: String,
            val city: String,
            val country: String,
            val postalCode: String,
            val name: String,
            val isDefault: Boolean,
            val latitude: Double,
            val longitude: Double,
            val recipientName: String,
            val phone: String,
            val addressId: String? = null
        ) : Intent()
        data object DismissSuccessMessage : Intent()
        data object ClearError : Intent()
        data object NavigateBack : Intent()
    }

    sealed class Effect {
        data object NavigateBack : Effect()
        data class ShowMessage(val message: String) : Effect()
        data class MoveCameraToLocation(val latitude: Double, val longitude: Double) : Effect()
        data class NavigateToAddressValidation(
            val latitude: Double,
            val longitude: Double,
            val street: String,
            val city: String,
            val country: String,
            val postalCode: String,
            val label: String,
            val isDefault: Boolean,
            val recipientName: String,
            val phone: String,
            val addressId: String?
        ) : Effect()
    }
}
