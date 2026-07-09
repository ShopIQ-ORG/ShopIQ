package com.iti.presentation.screens.checkout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.BuildConfig
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.components.showInfo
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.address.AddressContract
import com.iti.presentation.screens.address.AddressScreen
import com.iti.presentation.screens.address.AddressViewModel
import com.iti.presentation.screens.checkout.components.CheckoutStepper
import com.iti.presentation.screens.checkout.components.SummaryStepContent
import com.iti.presentation.screens.checkout.components.SuccessStepContent
import com.iti.presentation.screens.checkout.payment.PaymentScreen
import com.iti.presentation.screens.checkout.payment.PaymentViewModel
import org.koin.androidx.compose.koinViewModel
import com.iti.presentation.screens.checkout.components.PaymentMethodContent
import com.iti.presentation.util.CurrencyManager
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showOnlinePayment by remember { mutableStateOf(false) }
    val paymentSuccessfulText = stringResource(R.string.payment_successful)
    val context = LocalContext.current

    val showCheckoutHeaders = state.currentStep < 4 &&
            addressState.screenState !is AddressContract.ScreenState.MapPicker &&
            addressState.screenState !is AddressContract.ScreenState.LocationDetected

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CheckoutContract.Effect.NavigateBack -> onNavigateBack()
                CheckoutContract.Effect.NavigateToHome -> onNavigateToHome()
                is CheckoutContract.Effect.ShowError -> {
                    scope.launch {
                        snackbarHostState.showError(effect.message.resolve(context))
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (showCheckoutHeaders) {
                    BackTopBar(
                        title = when (state.currentStep) {
                            1 -> stringResource(R.string.checkout_step_address)
                            2 -> stringResource(R.string.checkout_step_payment_method)
                            3 -> stringResource(R.string.checkout_step_review_summary)
                            else -> stringResource(R.string.checkout_title)
                        },
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
                                snackbarHostState = snackbarHostState,
                                onAddressSelected = { address ->
                                    viewModel.onEvent(CheckoutContract.Event.AddressSelected(address))
                                }
                            )
                        }

                        2 -> {
                            PaymentMethodContent(
                                selectedMethod = state.paymentMethod,
                                onSelectMethod = { method ->
                                    viewModel.onEvent(CheckoutContract.Event.PaymentMethodSelected(method))
                                },
                                onContinue = {
                                    viewModel.onEvent(CheckoutContract.Event.PaymentMethodConfirmed)
                                },
                                isLoading = state.isLoading
                            )
                        }

                        3 -> {
                            SummaryStepContent(
                                cart = state.cart,
                                draftOrder = state.draftOrder,
                                shippingAddress = state.selectedAddress,
                                onPlaceOrder = {
                                    if (state.paymentMethod == PaymentMethodType.ONLINE) {
                                        showOnlinePayment = true
                                    } else {
                                        viewModel.onEvent(CheckoutContract.Event.PlaceOrder)
                                    }
                                },
                                isLoading = state.isLoading
                            )
                        }

                        4 -> {
                            state.draftOrder?.let { draftOrder ->
                                SuccessStepContent(
                                    draftOrder = draftOrder,
                                    currentUser = state.currentUser,
                                    onGoHome = onNavigateToHome
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showOnlinePayment) {
            val paymentViewModel: PaymentViewModel = koinViewModel()
            val amountUsd = state.cart?.total?.amount?.toDoubleOrNull() ?: 0.0
            val convertedAmount = CurrencyManager.convertFromUsd(amountUsd)
            val amountCents = (convertedAmount * 100).roundToLong()

            PaymentScreen(
                viewModel = paymentViewModel,
                amountCents = amountCents,
                integrationId = BuildConfig.PAYMOB_INTEGRATION_ID.toInt(),
                onPaymentSuccess = {
                    scope.launch {
                        snackbarHostState.showSuccess(paymentSuccessfulText)
                    }
                    viewModel.onEvent(CheckoutContract.Event.PlaceOrder)
                    showOnlinePayment = false
                },
                onPaymentFailure = { message ->
                    scope.launch {
                        snackbarHostState.showError(message)
                    }
                    showOnlinePayment = false
                },
                onShowInfo = { message ->
                    scope.launch {
                        snackbarHostState.showInfo(message)
                    }
                },
                onNavigateBack = {
                    showOnlinePayment = false
                }
            )
        }

        ShopIQSnackBarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }
}