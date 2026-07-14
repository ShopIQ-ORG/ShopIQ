package com.iti.presentation.screens.address

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Address
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.address.components.AddressEmptyState
import com.iti.presentation.screens.address.components.AddressListView
import com.iti.presentation.screens.address.components.AddressLocationDetected
import com.iti.presentation.screens.address.components.AddressMapPicker
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    onAddressSelected: ((Address) -> Unit)? = null
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val localSnackbarHostState = remember { SnackbarHostState() }
    val effectiveSnackbarHostState = snackbarHostState ?: localSnackbarHostState

    val successMessage = stringResource(R.string.address_success_added)

    val scope = rememberCoroutineScope()

    LaunchedEffect(state.showSuccessBadge) {
        if (state.showSuccessBadge) {
            viewModel.sendIntent(AddressContract.Intent.DismissSuccessBadge)
            scope.launch {
                effectiveSnackbarHostState.showSuccess(successMessage)
            }
        }
    }

    LaunchedEffect(state.errorText) {
        val message = state.errorText?.resolve(context)
        if (!message.isNullOrEmpty()) {
            effectiveSnackbarHostState.showError(message)
            viewModel.sendIntent(AddressContract.Intent.ClearError)
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
                    effectiveSnackbarHostState.showError(messageString)
                }
                else -> Unit
            }
        }
    }

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
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            if (onAddressSelected == null &&
                screenState !is AddressContract.ScreenState.MapPicker &&
                screenState !is AddressContract.ScreenState.LocationDetected) {
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
        val contentPadding = if (screenState is AddressContract.ScreenState.MapPicker ||
            screenState is AddressContract.ScreenState.LocationDetected) {
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
                            },
                            onAddressSelected = onAddressSelected
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

            if (snackbarHostState == null) {
                ShopIQSnackBarHost(
                    hostState = effectiveSnackbarHostState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}