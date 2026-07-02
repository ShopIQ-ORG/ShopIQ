//
//  AddressScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.screens.address.components.AddressEmptyState
import com.iti.presentation.screens.address.components.AddressGPSOnboarding
import com.iti.presentation.screens.address.components.AddressListView
import com.iti.presentation.screens.address.components.AddressLocationDetected
import com.iti.presentation.util.LocationPermissionHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddressContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is AddressContract.Effect.ShowMessage -> {
                    val messageString = effect.message.resolve(context)
                    snackbarHostState.showSnackbar(messageString)
                }
            }
        }
    }

    // Connect permission requests with standard system handler
    LocationPermissionHandler(
        onPermissionGranted = {
            viewModel.sendIntent(AddressContract.Intent.PermissionGranted)
        },
        onPermissionDenied = {
            viewModel.sendIntent(AddressContract.Intent.PermissionDenied)
        },
        triggerRequest = state.triggerPermissionRequest,
        onRequestHandled = {
            // Handled automatically through trigger request reset
        }
    )

    // Determine titles and top bar actions dynamically based on state
    val topBarTitle = when (state.screenState) {
        AddressContract.ScreenState.GPSOnboarding -> "Add New Address"
        is AddressContract.ScreenState.LocationDetected -> "Location Detected"
        else -> "Manage Addresses"
    }

    val topBarNavigationAction = {
        when (state.screenState) {
            AddressContract.ScreenState.GPSOnboarding,
            is AddressContract.ScreenState.LocationDetected -> {
                viewModel.sendIntent(AddressContract.Intent.CancelAddAddress)
            }
            else -> {
                viewModel.sendIntent(AddressContract.Intent.NavigateBack)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BackTopBar(
                title = topBarTitle,
                onBack = topBarNavigationAction,
                actions = {
                    val showAddIcon = when (state.screenState) {
                        AddressContract.ScreenState.Empty,
                        is AddressContract.ScreenState.Success -> true
                        else -> false
                    }
                    if (showAddIcon) {
                        IconButton(onClick = { viewModel.sendIntent(AddressContract.Intent.AddAddressClicked) }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Address",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screenState = state.screenState) {
                AddressContract.ScreenState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                AddressContract.ScreenState.Empty -> {
                    AddressEmptyState(
                        onAddNewAddressClick = {
                            viewModel.sendIntent(AddressContract.Intent.AddAddressClicked)
                        }
                    )
                }

                AddressContract.ScreenState.GPSOnboarding -> {
                    AddressGPSOnboarding(
                        onUseGPSClick = {
                            viewModel.sendIntent(AddressContract.Intent.RequestGPSLocation)
                        },
                        isDetecting = state.isDetectingLocation
                    )
                }

                is AddressContract.ScreenState.LocationDetected -> {
                    AddressLocationDetected(
                        address = screenState.address,
                        onConfirmClick = { tagName, isDefault ->
                            viewModel.sendIntent(
                                AddressContract.Intent.ConfirmAddress(tagName, isDefault)
                            )
                        },
                        onCancelClick = {
                            viewModel.sendIntent(AddressContract.Intent.RequestGPSLocation)
                        }
                    )
                }

                is AddressContract.ScreenState.Success -> {
                    AddressListView(
                        addresses = screenState.addresses,
                        showSuccessBadge = state.showSuccessBadge,
                        onDismissSuccessBadge = {
                            viewModel.sendIntent(AddressContract.Intent.DismissSuccessBadge)
                        },
                        onDeleteAddress = { id ->
                            viewModel.sendIntent(AddressContract.Intent.DeleteAddress(id))
                        },
                        onSetDefaultAddress = { id ->
                            viewModel.sendIntent(AddressContract.Intent.SetDefaultAddress(id))
                        }
                    )
                }

                is AddressContract.ScreenState.Failure -> {
                    ErrorScreen(
                        message = screenState.message,
                        onRetry = {
                            viewModel.sendIntent(AddressContract.Intent.LoadAddresses)
                        }
                    )
                }
            }
        }
    }
}
