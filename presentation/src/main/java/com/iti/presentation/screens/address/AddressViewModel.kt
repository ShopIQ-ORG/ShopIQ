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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.Result
import com.iti.domain.usecases.address.DeleteAddressUseCase
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.address.SaveAddressUseCase
import com.iti.domain.usecases.location.GetCurrentLocationUseCase
import com.iti.presentation.BuildConfig
import com.iti.presentation.R
import com.iti.presentation.util.LocationHelper
import com.iti.presentation.util.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.iti.domain.usecases.address.GetPlaceSuggestionsUseCase
import com.iti.domain.usecases.address.SearchLocationByNameUseCase

class AddressViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val getPlaceSuggestionsUseCase: GetPlaceSuggestionsUseCase,
    private val searchLocationByNameUseCase: SearchLocationByNameUseCase,
    @param:SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddressContract.State())
    val state: StateFlow<AddressContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddressContract.Effect>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val effect = _effect.asSharedFlow()

    private var temporaryDetectedAddress: Address? = null
    private var searchJob: Job? = null

    init {
        sendIntent(AddressContract.Intent.LoadAddresses)
    }

    fun sendIntent(intent: AddressContract.Intent) {
        when (intent) {
            AddressContract.Intent.LoadAddresses -> {
                loadAddresses()
            }
            AddressContract.Intent.AddAddressClicked -> {
                temporaryDetectedAddress = null
                _state.update {
                    it.copy(
                        screenState = AddressContract.ScreenState.MapPicker(30.0444, 31.2357)
                    )
                }
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
                        isDetectingLocation = false
                    )
                }
                emitEffect(AddressContract.Effect.ShowMessage(UiText.StringResource(R.string.error_network)))
            }
            is AddressContract.Intent.ConfirmAddress -> {
                val currentAddress = temporaryDetectedAddress
                if (currentAddress == null || currentAddress.latitude == 0.0 || currentAddress.longitude == 0.0) {
                    _state.update {
                        it.copy(
                            errorText = UiText.StringResource(R.string.address_error_location_missing)
                        )
                    }
                } else {
                    saveDetectedAddress(intent.name, intent.isDefault)
                }
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
            AddressContract.Intent.OpenMapPicker -> {
                val currentLat = temporaryDetectedAddress?.latitude ?: 30.0444
                val currentLng = temporaryDetectedAddress?.longitude ?: 31.2357
                _state.update {
                    it.copy(
                        screenState = AddressContract.ScreenState.MapPicker(currentLat, currentLng)
                    )
                }
            }
            is AddressContract.Intent.LocationSelectedFromMap -> {
                _state.update { it.copy(screenState = AddressContract.ScreenState.Loading) }
                viewModelScope.launch {
                    try {
                        val detected = LocationHelper.getAddressFromCoordinates(context, intent.latitude, intent.longitude)
                        temporaryDetectedAddress = detected
                        _state.update {
                            it.copy(
                                screenState = AddressContract.ScreenState.LocationDetected(detected, isFromGps = false)
                            )
                        }
                    } catch (_: Exception) {
                        emitEffect(AddressContract.Effect.ShowMessage(UiText.StringResource(R.string.address_error_geocoding_failed)))
                        _state.update {
                            it.copy(
                                screenState = AddressContract.ScreenState.MapPicker(intent.latitude, intent.longitude)
                            )
                        }
                    }
                }
            }
            AddressContract.Intent.CancelMapPicker -> {
                _state.update {
                    it.copy(
                        screenState = when (val current = temporaryDetectedAddress) {
                            null -> if (it.addresses.isEmpty()) AddressContract.ScreenState.Empty else AddressContract.ScreenState.Success(it.addresses)
                            else -> AddressContract.ScreenState.LocationDetected(current, isFromGps = false)
                        }
                    )
                }
            }
            is AddressContract.Intent.SearchQueryChanged -> {
                onSearchQueryChanged(intent.query)
            }
            AddressContract.Intent.ClearSuggestions -> {
                clearSuggestions()
            }
            AddressContract.Intent.ClearError -> {
                _state.update { it.copy(errorText = null) }
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
                        val addresses = result.data.filter { it.street.isNotBlank() || it.city.isNotBlank() }
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
            _state.update { it.copy(isDetectingLocation = false) }
            if (coords != null) {
                emitEffect(AddressContract.Effect.MoveCameraToLocation(coords.latitude, coords.longitude))
            } else {
                emitEffect(AddressContract.Effect.ShowMessage(UiText.StringResource(R.string.address_error_gps_failed)))
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

    private fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            val result = getPlaceSuggestionsUseCase(query, BuildConfig.MAPS_API_KEY)
            if (result is Result.Success) {
                val suggestions = result.data.map {
                    AddressContract.PlaceSuggestion(it.displayName, it.latitude, it.longitude)
                }
                _state.update { it.copy(searchSuggestions = suggestions) }
            }
        }
    }

    private fun clearSuggestions() {
        searchJob?.cancel()
        _state.update { it.copy(searchSuggestions = emptyList()) }
    }

    suspend fun searchLocationByName(query: String): LocationCoordinates? {
        val result = searchLocationByNameUseCase(query, BuildConfig.MAPS_API_KEY)
        return if (result is Result.Success) result.data else null
    }

    private fun emitEffect(effect: AddressContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}