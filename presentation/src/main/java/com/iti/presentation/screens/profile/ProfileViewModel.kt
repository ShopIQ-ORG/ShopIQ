//
//  ProfileViewModel.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.address.DeleteAddressUseCase
import com.iti.domain.usecases.address.GetPlaceSuggestionsUseCase
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.address.SaveAddressUseCase
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.auth.UpdateProfileUseCase
import com.iti.domain.usecases.currency.FetchExchangeRatesUseCase
import com.iti.domain.usecases.currency.GetExchangeRateHistoryUseCase
import com.iti.domain.usecases.currency.GetPopularCurrenciesUseCase
import com.iti.domain.usecases.currency.GetSelectedCurrencyUseCase
import com.iti.domain.usecases.currency.SelectCurrencyUseCase
import com.iti.domain.usecases.location.GetCurrentLocationUseCase
import com.iti.presentation.BuildConfig
import com.iti.presentation.R
import com.iti.presentation.util.LocationHelper
import com.iti.presentation.util.UiText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
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

class ProfileViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase,
    private val getPopularCurrenciesUseCase: GetPopularCurrenciesUseCase,
    private val getExchangeRateHistoryUseCase: GetExchangeRateHistoryUseCase,
    private val fetchExchangeRatesUseCase: FetchExchangeRatesUseCase,
    private val selectCurrencyUseCase: SelectCurrencyUseCase,
    private val getPlaceSuggestionsUseCase: GetPlaceSuggestionsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    @param:SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileContract.State())
    val state: StateFlow<ProfileContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileContract.Effect>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val effect = _effect.asSharedFlow()

    private var searchJob: Job? = null

    init {
        sendIntent(ProfileContract.Intent.LoadProfile)
        sendIntent(ProfileContract.Intent.LoadAddresses)
        observeCurrencyData()
        refreshRates()
    }

    private fun observeCurrencyData() {
        viewModelScope.launch {
            getSelectedCurrencyUseCase().collect { currency ->
                _state.update { it.copy(selectedCurrency = currency) }
                loadHistory(currency.code)
            }
        }
        viewModelScope.launch {
            getPopularCurrenciesUseCase().collect { currencies ->
                _state.update { it.copy(popularCurrencies = currencies) }
            }
        }
    }

    private fun refreshRates() {
        _state.update { it.copy(exchangeRateLoading = true) }
        viewModelScope.launch {
            fetchExchangeRatesUseCase()
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
            _state.update {
                it.copy(
                    exchangeRateLoading = false,
                    exchangeRatesLastUpdated = sdf.format(Date())
                )
            }
        }
    }

    private fun loadHistory(code: String) {
        viewModelScope.launch {
            getExchangeRateHistoryUseCase(code).collect { history ->
                _state.update { it.copy(exchangeRateHistory = history) }
            }
        }
    }

    fun sendIntent(intent: ProfileContract.Intent) {
        when (intent) {
            ProfileContract.Intent.LoadProfile -> {
                loadProfile()
            }
            is ProfileContract.Intent.UpdateProfile -> {
                updateProfile(
                    name = intent.fullName,
                    email = intent.email,
                    phone = intent.phone,
                    dob = intent.dateOfBirth,
                    gender = intent.gender,
                    avatarUrl = intent.avatarUrl
                )
            }
            is ProfileContract.Intent.ChangeCurrency -> {
                viewModelScope.launch {
                    selectCurrencyUseCase(intent.currencyCode)
                    emitEffect(ProfileContract.Effect.ShowCurrencyUpdatedMessage(intent.currencyCode))
                }
            }
            ProfileContract.Intent.RefreshExchangeRates -> {
                refreshRates()
            }
            is ProfileContract.Intent.LoadExchangeRateHistory -> {
                loadHistory(intent.currencyCode)
            }
            ProfileContract.Intent.LoadAddresses -> {
                loadAddresses()
            }
            is ProfileContract.Intent.DeleteAddress -> {
                deleteAddress(intent.addressId)
            }
            is ProfileContract.Intent.SetDefaultAddress -> {
                setDefaultAddress(intent.addressId)
            }
            is ProfileContract.Intent.SearchQueryChanged -> {
                searchPlaces(intent.query)
            }
            is ProfileContract.Intent.SelectPlaceSuggestion -> {
                selectPlaceSuggestion(intent.suggestion)
            }
            ProfileContract.Intent.RequestGPSLocation -> {
                _state.update { it.copy(triggerPermissionRequest = true, isDetectingLocation = true) }
            }
            ProfileContract.Intent.PermissionGranted -> {
                _state.update { it.copy(triggerPermissionRequest = false) }
                detectLocation()
            }
            ProfileContract.Intent.PermissionDenied -> {
                _state.update { it.copy(triggerPermissionRequest = false, isDetectingLocation = false) }
                emitEffect(ProfileContract.Effect.ShowMessage(UiText.StringResource(R.string.error_location_permission)))
            }
            is ProfileContract.Intent.ConfirmAddress -> {
                saveConfirmedAddress(
                    name = intent.name,
                    street = intent.street,
                    city = intent.city,
                    country = intent.country,
                    postalCode = intent.postalCode,
                    isDefault = intent.isDefault,
                    latitude = intent.latitude,
                    longitude = intent.longitude,
                    recipientName = intent.recipientName,
                    phone = intent.phone,
                    addressId = intent.addressId
                )
            }
            ProfileContract.Intent.ClearTempAddress -> {
                _state.update {
                    it.copy(
                        tempLatitude = null,
                        tempLongitude = null,
                        tempStreet = "",
                        tempCity = "",
                        tempCountry = "",
                        tempPostalCode = "",
                        shouldPopulateTempAddress = false
                    )
                }
            }
            ProfileContract.Intent.DismissSuccessMessage -> {
                _state.update { it.copy(successText = null) }
            }
            ProfileContract.Intent.ClearError -> {
                _state.update { it.copy(errorText = null) }
            }
            ProfileContract.Intent.NavigateBack -> {
                emitEffect(ProfileContract.Effect.NavigateBack)
            }
            ProfileContract.Intent.Logout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _state.update { it.copy(user = User.GuestUser) }
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _state.update { it.copy(user = result.data) }
                }
                else -> {
                    _state.update { it.copy(user = User.GuestUser) }
                }
            }
        }
    }

    private fun updateProfile(
        name: String,
        email: String,
        phone: String,
        dob: String,
        gender: String,
        avatarUrl: String?
    ) {
        if (name.isBlank() || email.isBlank()) {
            _state.update { it.copy(errorText = UiText.StringResource(R.string.error_name_email_required)) }
            return
        }
        _state.update { it.copy(isUpdatingProfile = true, errorText = null) }
        viewModelScope.launch {
            val result = updateProfileUseCase(
                fullName = name,
                phone = phone,
                dateOfBirth = dob.takeIf { it.isNotBlank() },
                gender = gender.takeIf { it.isNotBlank() },
                avatarUrl = avatarUrl
            )
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            user = result.data,
                            isUpdatingProfile = false
                        )
                    }
                    emitEffect(ProfileContract.Effect.ShowSuccessMessage)
                    emitEffect(ProfileContract.Effect.NavigateBack)
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isUpdatingProfile = false,
                            errorText = if (result.exception.message != null) UiText.Plain(result.exception.message!!) else UiText.StringResource(R.string.error_failed_update_profile)
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    private fun loadAddresses() {
        _state.update { it.copy(addressLoading = true) }
        viewModelScope.launch {
            getSavedAddressesUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val validAddresses = result.data.filter { it.street.isNotBlank() || it.city.isNotBlank() }
                        _state.update {
                            it.copy(
                                addresses = validAddresses,
                                addressLoading = false
                            )
                        }
                    }
                    is Result.Failure -> {
                        _state.update {
                            it.copy(
                                addressLoading = false,
                                errorText = if (result.exception.message != null) UiText.Plain(result.exception.message!!) else UiText.StringResource(R.string.error_failed_load_addresses)
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val result = deleteAddressUseCase(addressId)
            if (result is Result.Success) {
                emitEffect(ProfileContract.Effect.ShowMessage(UiText.StringResource(R.string.success_address_deleted)))
                loadAddresses()
            } else if (result is Result.Failure) {
                _state.update { it.copy(errorText = if (result.exception.message != null) UiText.Plain(result.exception.message!!) else UiText.StringResource(
                    R.string.error_failed_delete_address)) }
            }
        }
    }

    private fun setDefaultAddress(addressId: String) {
        val addressToUpdate = _state.value.addresses.firstOrNull { it.id == addressId } ?: return
        viewModelScope.launch {
            val result = saveAddressUseCase(addressToUpdate.copy(isDefault = true))
            if (result is Result.Success) {
                emitEffect(ProfileContract.Effect.ShowMessage(UiText.StringResource(R.string.success_default_address_updated)))
                loadAddresses()
            }
        }
    }

    private fun searchPlaces(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400) // Debounce 400ms
            val result = getPlaceSuggestionsUseCase(query, BuildConfig.MAPS_API_KEY)
            if (result is Result.Success) {
                val mapped = result.data.map {
                    ProfileContract.PlaceSuggestion(
                        displayName = it.displayName,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
                _state.update { it.copy(searchSuggestions = mapped) }
            } else {
                _state.update { it.copy(searchSuggestions = emptyList()) }
            }
        }
    }

    private fun selectPlaceSuggestion(suggestion: ProfileContract.PlaceSuggestion) {
        _state.update { it.copy(searchSuggestions = emptyList()) }
        viewModelScope.launch {
            try {
                val detected = LocationHelper.getAddressFromCoordinates(
                    context = context,
                    lat = suggestion.latitude,
                    lng = suggestion.longitude
                )

                emitEffect(
                    ProfileContract.Effect.NavigateToAddressValidation(
                        latitude = suggestion.latitude,
                        longitude = suggestion.longitude,
                        street = detected.street,
                        city = detected.city,
                        country = detected.country,
                        postalCode = detected.postalCode,
                        label = "",
                        isDefault = false,
                        recipientName = "",
                        phone = "",
                        addressId = null
                    )
                )
            } catch (e: Exception) {
                emitEffect(
                    ProfileContract.Effect.NavigateToAddressValidation(
                        latitude = suggestion.latitude,
                        longitude = suggestion.longitude,
                        street = suggestion.displayName.substringBefore(","),
                        city = "",
                        country = "",
                        postalCode = "",
                        label = "",
                        isDefault = false,
                        recipientName = "",
                        phone = "",
                        addressId = null
                    )
                )
            }
        }
    }

    private fun detectLocation() {
        viewModelScope.launch {
            val coords = getCurrentLocationUseCase()
            if (coords != null) {
                try {
                    val detected = LocationHelper.getAddressFromCoordinates(context, coords.latitude, coords.longitude)
                    _state.update {
                        it.copy(
                            isDetectingLocation = false,
                            detectedAddress = detected
                        )
                    }
                    emitEffect(
                        ProfileContract.Effect.NavigateToAddressValidation(
                            latitude = coords.latitude,
                            longitude = coords.longitude,
                            street = detected.street,
                            city = detected.city,
                            country = detected.country,
                            postalCode = detected.postalCode,
                            label = "",
                            isDefault = false,
                            recipientName = "",
                            phone = "",
                            addressId = null
                        )
                    )
                } catch (e: Exception) {
                    _state.update { it.copy(isDetectingLocation = false) }
                    emitEffect(
                        ProfileContract.Effect.NavigateToAddressValidation(
                            latitude = coords.latitude,
                            longitude = coords.longitude,
                            street = "",
                            city = "",
                            country = "",
                            postalCode = "",
                            label = "",
                            isDefault = false,
                            recipientName = "",
                            phone = "",
                            addressId = null
                        )
                    )
                }
            } else {
                _state.update { it.copy(isDetectingLocation = false) }
                emitEffect(ProfileContract.Effect.ShowMessage(UiText.StringResource(R.string.error_failed_gps)))
            }
        }
    }

    private fun saveConfirmedAddress(
        name: String,
        street: String,
        city: String,
        country: String,
        postalCode: String,
        isDefault: Boolean,
        latitude: Double,
        longitude: Double,
        recipientName: String,
        phone: String,
        addressId: String?
    ) {
        val id = addressId ?: UUID.randomUUID().toString()
        val address = Address(
            id = id,
            name = name.ifBlank { "Home" },
            street = street,
            city = city,
            country = country,
            postalCode = postalCode,
            latitude = latitude,
            longitude = longitude,
            isDefault = isDefault,
            recipientName = recipientName,
            phone = phone
        )
        viewModelScope.launch {
            val result = saveAddressUseCase(address)
            if (result is Result.Success) {
                val successRes = if (addressId == null) R.string.success_address_added else R.string.success_address_updated
                _state.update { it.copy(successText = UiText.StringResource(successRes)) }
                loadAddresses()
                emitEffect(ProfileContract.Effect.NavigateBack)
            } else if (result is Result.Failure) {
                val errorText = if (result.exception.message != null) UiText.Plain(result.exception.message!!) else UiText.StringResource(R.string.error_failed_save_address)
                _state.update { it.copy(errorText = errorText) }
                emitEffect(ProfileContract.Effect.ShowMessage(errorText))
            }
        }
    }

    fun updateTempAddressLocation(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val detected = LocationHelper.getAddressFromCoordinates(context, lat, lng)
                _state.update {
                    it.copy(
                        tempLatitude = lat,
                        tempLongitude = lng,
                        tempStreet = detected.street,
                        tempCity = detected.city,
                        tempCountry = detected.country,
                        tempPostalCode = detected.postalCode,
                        shouldPopulateTempAddress = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        tempLatitude = lat,
                        tempLongitude = lng,
                        tempStreet = "",
                        tempCity = "",
                        tempCountry = "",
                        tempPostalCode = "",
                        shouldPopulateTempAddress = true
                    )
                }
            }
        }
    }

    private fun emitEffect(effect: ProfileContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
