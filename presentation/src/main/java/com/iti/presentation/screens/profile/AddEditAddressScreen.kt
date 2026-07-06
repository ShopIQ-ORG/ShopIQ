//
//  AddEditAddressScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQTextField
import com.iti.presentation.screens.address.components.TopSnackbar
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressScreen(
    viewModel: ProfileViewModel,
    addressId: String?,
    onNavigateBack: () -> Unit,
    onNavigateToValidation: (Double, Double, String, String, String, String, String, Boolean, String, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateBack -> onNavigateBack()
                is ProfileContract.Effect.ShowMessage -> {
                    launch {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
                is ProfileContract.Effect.NavigateToAddressValidation -> {
                    onNavigateToValidation(
                        effect.latitude,
                        effect.longitude,
                        effect.street,
                        effect.city,
                        effect.country,
                        effect.postalCode,
                        state.detectedAddress?.name ?: "",
                        state.detectedAddress?.isDefault ?: false,
                        effect.recipientName,
                        effect.phone,
                        addressId
                    )
                }
                else -> Unit
            }
        }
    }

    AddEditAddressContent(
        state = state,
        addressId = addressId,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        onNavigateToValidation = onNavigateToValidation,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAddressContent(
    state: ProfileContract.State,
    addressId: String?,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToValidation: (Double, Double, String, String, String, String, String, Boolean, String, String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameTag by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var recipientName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var latitude by remember { mutableStateOf(30.0444) }
    var longitude by remember { mutableStateOf(31.2357) }

    var showValidationSnackbar by remember { mutableStateOf(false) }
    var validationSnackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(addressId, state.addresses) {
        if (addressId != null) {
            val address = state.addresses.firstOrNull { it.id == addressId }
            address?.let {
                nameTag = it.name
                street = it.street
                city = it.city
                country = it.country
                postalCode = it.postalCode
                isDefault = it.isDefault
                latitude = it.latitude
                longitude = it.longitude
                recipientName = it.recipientName
                phone = it.phone
            }
        }
    }

    LaunchedEffect(state.shouldPopulateTempAddress) {
        if (state.shouldPopulateTempAddress) {
            street = state.tempStreet
            city = state.tempCity
            country = state.tempCountry
            postalCode = state.tempPostalCode
            state.tempLatitude?.let { latitude = it }
            state.tempLongitude?.let { longitude = it }
            onIntent(ProfileContract.Intent.ClearTempAddress)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                BackTopBar(
                    title = if (addressId == null) "Add Address" else "Edit Address",
                    onBack = onNavigateBack
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Select Location on Map Option Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToValidation(
                                latitude,
                                longitude,
                                street,
                                city,
                                country,
                                postalCode,
                                nameTag,
                                isDefault,
                                recipientName,
                                phone,
                                addressId
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select Location on Map",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Picks coordinates and fills address fields automatically",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Address Details Form
                Text(
                    text = "Address Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    placeholder = "Recipient Name",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "Mobile Number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = nameTag,
                    onValueChange = { nameTag = it },
                    placeholder = "Address Label (e.g. Home, Work, Gym)",
                    leadingIcon = Icons.Default.Info
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = street,
                    onValueChange = { street = it },
                    placeholder = "Street Address",
                    leadingIcon = Icons.Default.Home
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = city,
                    onValueChange = { city = it },
                    placeholder = "City",
                    leadingIcon = Icons.Default.LocationOn
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = country,
                    onValueChange = { country = it },
                    placeholder = "Country",
                    leadingIcon = Icons.Default.LocationOn
                )

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    placeholder = "Postal Code / ZIP",
                    leadingIcon = Icons.Default.Info
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Set Default Switch
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Set as default delivery address",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Use this address for default delivery",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Single Validation/Save Button
                ShopIQButton(
                    text = if (addressId == null) "Add Address" else "Save Address",
                    onClick = {
                        val missingFields = mutableListOf<String>()
                        if (recipientName.isBlank()) missingFields.add("Recipient Name")
                        if (phone.isBlank()) missingFields.add("Mobile Number")
                        if (street.isBlank()) missingFields.add("Street Address")
                        if (city.isBlank()) missingFields.add("City")
                        if (country.isBlank()) missingFields.add("Country")

                        if (missingFields.isNotEmpty()) {
                            validationSnackbarMessage = "Required fields missing: ${missingFields.joinToString(", ")}"
                            showValidationSnackbar = true
                        } else {
                            onIntent(
                                ProfileContract.Intent.ConfirmAddress(
                                    name = nameTag.ifBlank { "Home" },
                                    street = street,
                                    city = city,
                                    country = country,
                                    postalCode = postalCode,
                                    isDefault = isDefault,
                                    latitude = latitude,
                                    longitude = longitude,
                                    recipientName = recipientName,
                                    phone = phone,
                                    addressId = addressId
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Dropdown top snackbar for missing fields validation error (consistent with address features)
        TopSnackbar(
            message = validationSnackbarMessage,
            visible = showValidationSnackbar,
            onDismiss = { showValidationSnackbar = false },
            isError = true,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditAddressScreenPreview() {
    MaterialTheme {
        AddEditAddressContent(
            state = ProfileContract.State(),
            addressId = null,
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {},
            onNavigateToValidation = { _, _, _, _, _, _, _, _, _, _, _ -> }
        )
    }
}
