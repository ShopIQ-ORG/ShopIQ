//
//  AddressScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.screens.address.components.AddressEmptyState
import com.iti.presentation.screens.address.components.AddressListView
import com.iti.presentation.screens.address.components.AddressLocationDetected
import com.iti.presentation.screens.address.components.AddressMapPicker
import com.iti.presentation.screens.address.components.TopSnackbar
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

    var isSuccessSnackbarVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.showSuccessBadge) {
        if (state.showSuccessBadge) {
            kotlinx.coroutines.delay(600)
            isSuccessSnackbarVisible = true
        } else {
            isSuccessSnackbarVisible = false
        }
    }

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
                else -> Unit
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
    )

    // Determine titles and top bar actions dynamically based on state
    val screenState = state.screenState
    val topBarTitle = when (screenState) {
        is AddressContract.ScreenState.LocationDetected -> {
            if (screenState.isFromGps) {
                stringResource(R.string.address_location_detected_title)
            } else {
                stringResource(R.string.address_heading_selected)
            }
        }
        else -> stringResource(R.string.address_title)
    }

    val topBarNavigationAction = {
        when (screenState) {
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
            if (screenState !is AddressContract.ScreenState.MapPicker) {
                BackTopBar(
                    title = topBarTitle,
                    onBack = topBarNavigationAction,
                    actions = {
                        val showAddIcon = when (screenState) {
                            AddressContract.ScreenState.Empty,
                            is AddressContract.ScreenState.Success -> true
                            else -> false
                        }
                        if (showAddIcon) {
                            IconButton(onClick = { viewModel.sendIntent(AddressContract.Intent.AddAddressClicked) }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.address_action_add),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val contentPadding = if (screenState is AddressContract.ScreenState.MapPicker) {
            PaddingValues(0.dp)
        } else {
            innerPadding
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            AnimatedContent(
                targetState = screenState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "ScreenStateTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetState ->
                when (targetState) {
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

                    is AddressContract.ScreenState.LocationDetected -> {
                        AddressLocationDetected(
                            address = targetState.address,
                            isFromGps = targetState.isFromGps,
                            onConfirmClick = { tagName, isDefault ->
                                viewModel.sendIntent(
                                    AddressContract.Intent.ConfirmAddress(tagName, isDefault)
                                )
                            },
                            onEditLocationClick = {
                                viewModel.sendIntent(AddressContract.Intent.OpenMapPicker)
                            }
                        )
                    }

                    is AddressContract.ScreenState.MapPicker -> {
                        AddressMapPicker(
                            initialLatitude = targetState.initialLatitude,
                            initialLongitude = targetState.initialLongitude,
                            onLocationConfirmed = { lat, lng ->
                                viewModel.sendIntent(
                                    AddressContract.Intent.LocationSelectedFromMap(lat, lng)
                                )
                            },
                            onBackClick = {
                                viewModel.sendIntent(AddressContract.Intent.CancelMapPicker)
                            },
                            viewModel = viewModel
                        )
                    }

                    is AddressContract.ScreenState.Success -> {
                        AddressListView(
                            addresses = targetState.addresses,
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
                            message = targetState.message,
                            onRetry = {
                                viewModel.sendIntent(AddressContract.Intent.LoadAddresses)
                            }
                        )
                    }
                }
            }

            TopSnackbar(
                message = stringResource(R.string.address_success_added),
                visible = isSuccessSnackbarVisible,
                onDismiss = { viewModel.sendIntent(AddressContract.Intent.DismissSuccessBadge) },
                isError = false,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Top floating error snackbar overlay
            TopSnackbar(
                message = state.errorText?.resolve(context) ?: "",
                visible = state.errorText != null,
                onDismiss = { viewModel.sendIntent(AddressContract.Intent.ClearError) },
                isError = true,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
