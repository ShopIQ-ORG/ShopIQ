//
//  AddressViewModel.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.usecases.address.DeleteAddressUseCase
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.address.SaveAddressUseCase
import com.iti.domain.usecases.location.GetCurrentLocationUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class AddressViewModel @SuppressLint("StaticFieldLeak") constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddressContract.State())
    val state: StateFlow<AddressContract.State> = _state.asStateFlow()

    private val _effect = Channel<AddressContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var temporaryDetectedAddress: Address? = null

    init {
        sendIntent(AddressContract.Intent.LoadAddresses)
    }

    fun sendIntent(intent: AddressContract.Intent) {
        when (intent) {
            AddressContract.Intent.LoadAddresses -> {
                loadAddresses()
            }
            AddressContract.Intent.AddAddressClicked -> {
                _state.update { it.copy(screenState = AddressContract.ScreenState.GPSOnboarding) }
            }
            AddressContract.Intent.RequestGPSLocation -> {
                _state.update { it.copy(triggerPermissionRequest = true, isDetectingLocation = true) }
            }
            AddressContract.Intent.PermissionGranted -> {
                _state.update { it.copy(triggerPermissionRequest = false) }
                detectLocation()
            }
            AddressContract.Intent.PermissionDenied -> {
                _state.update { 
                    it.copy(
                        triggerPermissionRequest = false,
                        isDetectingLocation = false,
                        screenState = AddressContract.ScreenState.GPSOnboarding
                    )
                }
                emitEffect(AddressContract.Effect.ShowMessage(UiText.StringResource(R.string.error_network)))
            }
            is AddressContract.Intent.ConfirmAddress -> {
                saveDetectedAddress(intent.name, intent.isDefault)
            }
            is AddressContract.Intent.DeleteAddress -> {
                deleteAddress(intent.addressId)
            }
            is AddressContract.Intent.SetDefaultAddress -> {
                setDefaultAddress(intent.addressId)
            }
            AddressContract.Intent.DismissSuccessBadge -> {
                _state.update { it.copy(showSuccessBadge = false) }
            }
            AddressContract.Intent.CancelAddAddress -> {
                _state.update {
                    it.copy(
                        screenState = if (it.addresses.isEmpty()) {
                            AddressContract.ScreenState.Empty
                        } else {
                            AddressContract.ScreenState.Success(it.addresses)
                        },
                        isDetectingLocation = false
                    )
                }
            }
            AddressContract.Intent.NavigateBack -> {
                emitEffect(AddressContract.Effect.NavigateBack)
            }
        }
    }

    private fun loadAddresses() {
        _state.update { it.copy(screenState = AddressContract.ScreenState.Loading) }
        viewModelScope.launch {
            getSavedAddressesUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(screenState = AddressContract.ScreenState.Loading) }
                    }
                    is Result.Success -> {
                        val addresses = result.data
                        _state.update {
                            it.copy(
                                addresses = addresses,
                                screenState = if (addresses.isEmpty()) {
                                    AddressContract.ScreenState.Empty
                                } else {
                                    AddressContract.ScreenState.Success(addresses)
                                }
                            )
                        }
                    }
                    is Result.Failure -> {
                        val errorMsg = result.exception.message?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_unknown)
                        _state.update { it.copy(screenState = AddressContract.ScreenState.Failure(errorMsg)) }
                    }
                }
            }
        }
    }

    private fun detectLocation() {
        viewModelScope.launch {
            val coords = getCurrentLocationUseCase()
            if (coords != null) {
                val detected = getAddressFromCoordinates(coords.latitude, coords.longitude)
                temporaryDetectedAddress = detected
                _state.update {
                    it.copy(
                        isDetectingLocation = false,
                        screenState = AddressContract.ScreenState.LocationDetected(detected)
                    )
                }
            } else {
                // Fallback to simulated Baker street location
                val detected = getAddressFromCoordinates(51.52377, -0.15855)
                temporaryDetectedAddress = detected
                _state.update {
                    it.copy(
                        isDetectingLocation = false,
                        screenState = AddressContract.ScreenState.LocationDetected(detected)
                    )
                }
            }
        }
    }

    private fun saveDetectedAddress(name: String, isDefault: Boolean) {
        val detected = temporaryDetectedAddress ?: return
        val finalAddress = detected.copy(
            name = name,
            isDefault = isDefault
        )
        viewModelScope.launch {
            val result = saveAddressUseCase(finalAddress)
            if (result is Result.Success) {
                _state.update { it.copy(showSuccessBadge = true) }
                loadAddresses()
            } else if (result is Result.Failure) {
                val errorMsg = result.exception.message?.let { UiText.Plain(it) }
                    ?: UiText.StringResource(R.string.error_unknown)
                emitEffect(AddressContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val result = deleteAddressUseCase(addressId)
            if (result is Result.Success) {
                loadAddresses()
            } else if (result is Result.Failure) {
                val errorMsg = result.exception.message?.let { UiText.Plain(it) }
                    ?: UiText.StringResource(R.string.error_unknown)
                emitEffect(AddressContract.Effect.ShowMessage(errorMsg))
            }
        }
    }

    private fun setDefaultAddress(addressId: String) {
        val currentAddresses = _state.value.addresses
        val addressToUpdate = currentAddresses.firstOrNull { it.id == addressId } ?: return
        viewModelScope.launch {
            val result = saveAddressUseCase(addressToUpdate.copy(isDefault = true))
            if (result is Result.Success) {
                loadAddresses()
            }
        }
    }

    private suspend fun getAddressFromCoordinates(lat: Double, lng: Double): Address {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                // Use fallback geocoder approach
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addressObj = addresses[0]
                    val street = addressObj.thoroughfare ?: addressObj.subThoroughfare ?: addressObj.getAddressLine(0) ?: "221B Baker Street"
                    val city = addressObj.locality ?: addressObj.subAdminArea ?: "Near Regent's Park"
                    val postalCode = addressObj.postalCode ?: "London NW1 6XE"
                    val country = addressObj.countryName ?: "United Kingdom"
                    Address(
                        id = UUID.randomUUID().toString(),
                        name = "Home",
                        street = street,
                        city = city,
                        postalCode = postalCode,
                        country = country,
                        latitude = lat,
                        longitude = lng,
                        isDefault = false
                    )
                } else {
                    throw Exception("No address found")
                }
            } catch (e: Exception) {
                Address(
                    id = UUID.randomUUID().toString(),
                    name = "Home",
                    street = "221B Baker Street",
                    city = "Near Regent's Park",
                    postalCode = "London NW1 6XE",
                    country = "United Kingdom",
                    latitude = lat,
                    longitude = lng,
                    isDefault = false
                )
            }
        }
    }

    private fun emitEffect(effect: AddressContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
