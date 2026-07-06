//
//  AddressManagementScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Address
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.screens.address.components.AddressEmptyState
import com.iti.presentation.screens.address.components.AddressItem
import com.iti.presentation.screens.address.components.TopSnackbar
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddAddress: () -> Unit,
    onNavigateToEditAddress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.sendIntent(ProfileContract.Intent.LoadAddresses)
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateBack -> onNavigateBack()
                is ProfileContract.Effect.ShowMessage -> {
                    launch {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
                else -> Unit
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AddressManagementContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::sendIntent,
            onNavigateBack = onNavigateBack,
            onNavigateToAddAddress = onNavigateToAddAddress,
            onNavigateToEditAddress = onNavigateToEditAddress,
            modifier = Modifier.fillMaxSize()
        )

        TopSnackbar(
            message = state.successText ?: "",
            visible = state.successText != null,
            onDismiss = { viewModel.sendIntent(ProfileContract.Intent.DismissSuccessMessage) },
            isError = false,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementContent(
    state: ProfileContract.State,
    snackbarHostState: SnackbarHostState,
    onIntent: (ProfileContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAddAddress: () -> Unit,
    onNavigateToEditAddress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var addressToDelete by remember { mutableStateOf<Address?>(null) }

    if (addressToDelete != null) {
        ConfirmationDialog(
            title = stringResource(R.string.address_delete_dialog_title),
            message = stringResource(R.string.address_delete_confirm_msg),
            confirmText = stringResource(R.string.address_delete_btn),
            dismissText = stringResource(R.string.address_cancel_btn),
            onConfirm = {
                addressToDelete?.id?.let { id ->
                    onIntent(ProfileContract.Intent.DeleteAddress(id))
                }
                addressToDelete = null
            },
            onDismiss = { addressToDelete = null }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BackTopBar(
                title = stringResource(R.string.address_delivery_addresses),
                onBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToAddAddress) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.address_add_new_title),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.addressLoading && state.addresses.isEmpty()) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            } else if (state.addresses.isEmpty()) {
                AddressEmptyState(onAddNewAddressClick = onNavigateToAddAddress)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    items(state.addresses, key = { it.id }) { address ->
                        AddressItem(
                            address = address,
                            onEdit = { onNavigateToEditAddress(address.id) },
                            onDelete = { addressToDelete = address },
                            onSetDefault = { onIntent(ProfileContract.Intent.SetDefaultAddress(address.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddressManagementScreenPreview() {
    MaterialTheme {
        AddressManagementContent(
            state = ProfileContract.State(
                addresses = listOf(
                    Address("1", "Home", "123 Main St", "Cairo", "Egypt", "12345", 30.044, 31.235, isDefault = true),
                    Address("2", "Work", "456 Office Rd", "Giza", "Egypt", "54321", 30.010, 31.210, isDefault = false)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {},
            onNavigateToAddAddress = {},
            onNavigateToEditAddress = {}
        )
    }
}
