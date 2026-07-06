//
//  CheckoutScreen.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.checkout.DraftOrder
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.screens.address.AddressContract
import com.iti.presentation.screens.address.AddressScreen
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.screens.checkout.components.CheckoutStepper
import com.iti.presentation.screens.checkout.components.PaymentStepContent
import com.iti.presentation.screens.checkout.components.SummaryStepContent
import com.iti.presentation.screens.checkout.components.SuccessStepContent
import com.iti.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    addressViewModel: AddressViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val addressState by addressViewModel.state.collectAsState()

    val showCheckoutHeaders = state.currentStep < 4 &&
            addressState.screenState !is AddressContract.ScreenState.MapPicker &&
            addressState.screenState !is AddressContract.ScreenState.LocationDetected

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CheckoutContract.Effect.NavigateBack -> onNavigateBack()
                CheckoutContract.Effect.NavigateToHome -> onNavigateToHome()
                is CheckoutContract.Effect.ShowError -> {
                    // Handled inside Scaffold snackbar or Toast if needed
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showCheckoutHeaders) {
                BackTopBar(
                    title = stringResource(R.string.checkout_title),
                    onBack = { viewModel.onEvent(CheckoutContract.Event.NavigateBack) },
                    actions = {
                        if (state.currentStep == 1) {
                            IconButton(onClick = { addressViewModel.sendIntent(AddressContract.Intent.AddAddressClicked) }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showCheckoutHeaders) innerPadding else PaddingValues(0.dp))
        ) {
            if (showCheckoutHeaders) {
                CheckoutStepper(currentStep = state.currentStep)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (state.currentStep) {
                    1 -> {
                        AddressScreen(
                            viewModel = addressViewModel,
                            onNavigateBack = onNavigateBack,
                            onAddressSelected = { address ->
                                viewModel.onEvent(CheckoutContract.Event.AddressSelected(address))
                            }
                        )
                    }

                    2 -> {
                        PaymentStepContent(
                            onConfirm = {
                                viewModel.onEvent(CheckoutContract.Event.PaymentConfirmed)
                            }
                        )
                    }

                    3 -> {
                        SummaryStepContent(
                            cart = state.cart,
                            draftOrder = state.draftOrder,
                            shippingAddress = state.selectedAddress,
                            onPlaceOrder = {
                                viewModel.onEvent(CheckoutContract.Event.PlaceOrder)
                            }
                        )
                    }

                    4 -> {
                        SuccessStepContent(
                            draftOrder = state.draftOrder,
                            currentUser = state.currentUser,
                            onGoHome = onNavigateToHome
                        )
                    }
                }

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
