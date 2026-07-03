package com.iti.presentation.screens.address

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.Result
import com.iti.domain.usecases.address.DeleteAddressUseCase
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.address.SaveAddressUseCase
import com.iti.domain.usecases.location.GetCurrentLocationUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AddressViewModel @SuppressLint("StaticFieldLeak") constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val saveAddressUseCase: SaveAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    @SuppressLint("StaticFieldLeak") private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddressContract.State())
    val state: StateFlow<AddressContract.State> = _state.asStateFlow()

    private val _effect = Channel<AddressContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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
                        val detected = getAddressFromCoordinates(intent.latitude, intent.longitude)
                        temporaryDetectedAddress = detected
                        _state.update {
                            it.copy(
                                screenState = AddressContract.ScreenState.LocationDetected(detected)
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
                            null -> AddressContract.ScreenState.GPSOnboarding
                            else -> AddressContract.ScreenState.LocationDetected(current)
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
                try {
                    val detected = getAddressFromCoordinates(coords.latitude, coords.longitude)
                    temporaryDetectedAddress = detected
                    _state.update {
                        it.copy(
                            isDetectingLocation = false,
                            screenState = AddressContract.ScreenState.LocationDetected(detected)
                        )
                    }
                } catch (_: Exception) {
                    _state.update {
                        it.copy(
                            isDetectingLocation = false,
                            screenState = AddressContract.ScreenState.Failure(
                                UiText.StringResource(R.string.address_error_geocoding_failed)
                            )
                        )
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        isDetectingLocation = false,
                        screenState = AddressContract.ScreenState.Failure(
                            UiText.StringResource(R.string.address_error_gps_failed)
                        )
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
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<android.location.Address>) {
                            if (addresses.isNotEmpty()) {
                                continuation.resume(mapAndroidAddressToDomain(addresses[0], lat, lng))
                            } else {
                                continuation.resumeWithException(Exception("No address found"))
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            continuation.resumeWithException(Exception(errorMessage ?: "Geocoding error"))
                        }
                    })
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    mapAndroidAddressToDomain(addresses[0], lat, lng)
                } else {
                    throw Exception("No address found")
                }
            }
        }
    }

    private fun mapAndroidAddressToDomain(
        addressObj: android.location.Address,
        lat: Double,
        lng: Double
    ): Address {
        val street = addressObj.thoroughfare ?: addressObj.subThoroughfare ?: addressObj.getAddressLine(0) ?: ""
        val city = addressObj.locality ?: addressObj.subAdminArea ?: ""
        val postalCode = addressObj.postalCode ?: ""
        val country = addressObj.countryName ?: ""
        return Address(
            id = UUID.randomUUID().toString(),
            name = "",
            street = street,
            city = city,
            postalCode = postalCode,
            country = country,
            latitude = lat,
            longitude = lng,
            isDefault = false
        )
    }

    private fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(500) // Debounce 500ms
            val suggestions = getSuggestionsFromNominatim(query)
            _state.update { it.copy(searchSuggestions = suggestions) }
        }
    }

    private fun clearSuggestions() {
        searchJob?.cancel()
        _state.update { it.copy(searchSuggestions = emptyList()) }
    }

    suspend fun searchLocationByName(query: String): LocationCoordinates? {
        // Try system Geocoder first
        val systemResult = searchLocationByNameWithGeocoder(query)
        if (systemResult != null) return systemResult

        // Fallback to Nominatim search
        val suggestions = getSuggestionsFromNominatim(query)
        if (suggestions.isNotEmpty()) {
            return LocationCoordinates(suggestions[0].latitude, suggestions[0].longitude)
        }
        return null
    }

    private suspend fun searchLocationByNameWithGeocoder(query: String): LocationCoordinates? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(query, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<android.location.Address>) {
                                if (addresses.isNotEmpty()) {
                                    val addressObj = addresses[0]
                                    continuation.resume(LocationCoordinates(addressObj.latitude, addressObj.longitude))
                                } else {
                                    continuation.resume(null)
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                continuation.resume(null)
                            }
                        })
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(query, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addressObj = addresses[0]
                        LocationCoordinates(addressObj.latitude, addressObj.longitude)
                    } else {
                        null
                    }
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    private suspend fun getSuggestionsFromNominatim(query: String): List<AddressContract.PlaceSuggestion> {
        return withContext(Dispatchers.IO) {
            val suggestions = mutableListOf<AddressContract.PlaceSuggestion>()
            var connection: HttpURLConnection? = null
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val url = URL("https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=5&accept-language=en")
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "ShopIQ-Android-App")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonArray = JSONArray(response.toString())
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val displayName = jsonObject.getString("display_name")
                        val lat = jsonObject.getDouble("lat")
                        val lon = jsonObject.getDouble("lon")
                        suggestions.add(AddressContract.PlaceSuggestion(displayName, lat, lon))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
            suggestions
        }
    }

    private fun emitEffect(effect: AddressContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
