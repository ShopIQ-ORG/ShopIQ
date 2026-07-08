package com.iti.presentation.screens.payment

import android.widget.Toast
import java.util.HashMap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener
import com.iti.presentation.R

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    amountCents: Long,
    currency: String = "EGP",
    integrationId: Int,
    onPaymentSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.paymentUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val paymentSuccessfulText = stringResource(id = R.string.payment_successful)
    val paymentPendingText = stringResource(id = R.string.payment_pending)

    // Auto-start payment flow on entry
    LaunchedEffect(Unit) {
        viewModel.startPaymentFlow(
            amountCents = amountCents,
            currency = currency,
            integrationId = integrationId
        )
    }

    LaunchedEffect(uiState) {
        if (uiState is PaymentUiState.Success) {
            val successState = uiState as PaymentUiState.Success

            try {
                PaymobSdk.Builder(
                    context = context,
                    clientSecret = successState.clientSecret,
                    publicKey = successState.publicKey,
                    paymobSdkListener = object : PaymobSdkListener {
                        override fun onSuccess(payResponse: HashMap<String, String?>) {
                            viewModel.resetState()
                            scope.launch { snackbarHostState.showSnackbar(paymentSuccessfulText) }
                            onPaymentSuccess()
                        }

                        override fun onFailure(msg: String?) {
                            viewModel.resetState()
                            val failureText = context.getString(R.string.payment_failed, msg ?: "")
                            scope.launch { snackbarHostState.showSnackbar(failureText) }
                        }

                        override fun onPending() {
                            viewModel.resetState()
                            scope.launch { snackbarHostState.showSnackbar(paymentPendingText) }
                        }
                    }
                ).build().start()
            } catch (e: Exception) {
                viewModel.resetState()
                val errorText = context.getString(R.string.payment_sdk_error, e.message ?: "")
                scope.launch { snackbarHostState.showSnackbar(errorText) }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is PaymentUiState.Loading -> CircularProgressIndicator()
            is PaymentUiState.Error -> {
                Text(text = (uiState as PaymentUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            is PaymentUiState.Success -> {
                // SDK should be launching via LaunchedEffect
            }
            else -> {
                Button(
                    onClick = {
                        viewModel.startPaymentFlow(
                            amountCents = amountCents,
                            currency = currency,
                            integrationId = integrationId
                        )
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.pay_now_amount, amountCents / 100.0))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}