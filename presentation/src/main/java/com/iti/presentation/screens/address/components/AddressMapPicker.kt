//
//  AddressMapPicker.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.iti.presentation.screens.address.components

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.screens.address.AddressContract
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.ui.theme.BackgroundDark
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressMapPicker(
    initialLatitude: Double,
    initialLongitude: Double,
    onLocationConfirmed: (latitude: Double, longitude: Double) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddressViewModel?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    val state = viewModel?.state?.collectAsState()?.value ?: AddressContract.State()

    // Setup camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(initialLatitude, initialLongitude), 17f)
    }

    var lastGeocodedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Sync camera panning with search bar query (debounced)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            val last = lastGeocodedLatLng
            if (last == null || Math.abs(center.latitude - last.latitude) > 0.0001 || Math.abs(center.longitude - last.longitude) > 0.0001) {
                delay(800) // Debounce
                try {
                    val address = LocationHelper.getAddressFromCoordinates(context, center.latitude, center.longitude)
                    val addressStr = listOfNotNull(
                        address.street.takeIf { it.isNotBlank() },
                        address.city.takeIf { it.isNotBlank() },
                        address.country.takeIf { it.isNotBlank() }
                    ).joinToString(", ")
                    
                    searchQuery = addressStr
                    lastGeocodedLatLng = center
                } catch (e: Exception) {
                    // Ignore geocoding errors during map panning
                }
            }
        }
    }

    // Collect effects from ViewModel (e.g. MoveCameraToLocation when GPS finishes fetching)
    LaunchedEffect(viewModel) {
        viewModel?.effect?.collect { effect ->
            when (effect) {
                is AddressContract.Effect.MoveCameraToLocation -> {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(effect.latitude, effect.longitude),
                            17f
                        )
                    )
                }
                else -> {}
            }
        }
    }

    val performSearch = {
        if (searchQuery.isNotBlank() && viewModel != null) {
            isSearching = true
            searchError = null
            keyboardController?.hide()
            viewModel.sendIntent(AddressContract.Intent.ClearSuggestions)
            coroutineScope.launch {
                val resultCoords = viewModel.searchLocationByName(searchQuery)
                isSearching = false
                if (resultCoords != null) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(resultCoords.latitude, resultCoords.longitude),
                            17f
                        )
                    )
                } else {
                    searchError = context.getString(R.string.address_error_location_not_found)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = stringResource(R.string.address_select_location_title),
                onBack = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Google Map View
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = false
                )
            )

            // Center Pin Overlay (map pans beneath this pin)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Drop Shadow / Small Circle
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f))
                )
                // Floating Marker Pin
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = BackgroundDark,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-24).dp)
                )
            }

            // Top Search Bar & Suggestions Overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search query",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it 
                            searchError = null
                            viewModel?.sendIntent(AddressContract.Intent.SearchQueryChanged(it))
                        },
                        placeholder = { Text(stringResource(R.string.address_search_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { performSearch() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { performSearch() }) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.address_gps_search),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Suggestions List
                val suggestions = state.searchSuggestions
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            suggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = suggestion.displayName,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyMedium
                                        ) 
                                    },
                                    onClick = {
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(suggestion.latitude, suggestion.longitude),
                                                    17f
                                                )
                                            )
                                        }
                                        searchQuery = suggestion.displayName
                                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                                        keyboardController?.hide()
                                    }
                                )
                            }
                        }
                    }
                }

                searchError?.let { errorText ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = errorText,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }

            // GPS Floating Circular Button (Bottom Right, above confirmation button)
            FloatingActionButton(
                onClick = {
                    viewModel?.sendIntent(AddressContract.Intent.RequestGPSLocation)
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 96.dp, end = 24.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Current Location",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Bottom Confirmation Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                ShopIQButton(
                    text = stringResource(R.string.address_btn_confirm_location),
                    onClick = {
                        viewModel?.sendIntent(AddressContract.Intent.ClearSuggestions)
                        val centerPoint = cameraPositionState.position.target
                        onLocationConfirmed(centerPoint.latitude, centerPoint.longitude)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun AddressMapPickerLightPreview() {
    ShopIQTheme(darkTheme = false) {
        AddressMapPicker(
            initialLatitude = 30.0444,
            initialLongitude = 31.2357,
            onLocationConfirmed = { _, _ -> },
            onBackClick = {},
            viewModel = null
        )
    }
}
