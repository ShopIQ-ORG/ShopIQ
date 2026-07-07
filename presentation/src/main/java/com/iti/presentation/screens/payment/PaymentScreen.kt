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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    amountCents: Long,
    currency: String = "EGP",
    integrationId: Int
) {
    val context = LocalContext.current
    val uiState by viewModel.paymentUiState.collectAsState()

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
                            Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG).show()
                        }

                        override fun onFailure(msg: String?) {
                            viewModel.resetState()
                            Toast.makeText(context, "Payment Failed: $msg", Toast.LENGTH_LONG).show()
                        }

                        override fun onPending() {
                            viewModel.resetState()
                            Toast.makeText(context, "Payment Pending", Toast.LENGTH_LONG).show()
                        }
                    }
                ).build().start()
            } catch (e: Exception) {
                viewModel.resetState()
                Toast.makeText(context, "Error starting SDK: ${e.message}", Toast.LENGTH_LONG).show()
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
                    Text(text = "Pay Now $${amountCents / 100.0}")
                }
            }
        }
    }
}
