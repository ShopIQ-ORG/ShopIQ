package com.iti.presentation.screens.checkout.payment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.util.CurrencyManager
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.ui.PaymobSdkListener
import java.util.HashMap
import org.koin.androidx.compose.koinViewModel

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = koinViewModel(),
    amountCents: Long,
    integrationId: Int,
    onPaymentSuccess: () -> Unit,
    onPaymentFailure: (String) -> Unit,
    onShowInfo: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.paymentUiState.collectAsState()
    val paymentPendingText = stringResource(R.string.payment_pending)
    val paymentFailedFormat = stringResource(R.string.payment_failed)
    val paymentSdkErrorFormat = stringResource(R.string.payment_sdk_error)
    val currencyConversionNoticeText = stringResource(R.string.payment_currency_not_supported)

    DisposableEffect(Unit) {
        Log.d("PAYMOB", "PaymentScreen Created")
        onDispose {
            Log.d("PAYMOB", "PaymentScreen Disposed")
        }
    }

    LaunchedEffect(Unit) {
        onShowInfo(currencyConversionNoticeText)
        viewModel.startPaymentFlow(
            amountCents = CurrencyManager.convertCentsToEgp(amountCents),
            currency = "EGP",
            integrationId = integrationId
        )
    }
    LaunchedEffect(uiState) {
        when (val current = uiState) {
            is PaymentUiState.Success -> {
                try {
                    PaymobSdk.Builder(
                        context = context,
                        clientSecret = current.clientSecret,
                        publicKey = current.publicKey,
                        paymobSdkListener = object : PaymobSdkListener {
                            override fun onSuccess(payResponse: HashMap<String, String?>) {
                                viewModel.resetState()
                                Log.d("PAYMOB", "onSuccess: $payResponse")

                                onPaymentSuccess()
                            }

                            override fun onFailure(msg: String?) {
                                viewModel.resetState()
                                Log.d("PAYMOB", "onFailure: $msg")

                                onPaymentFailure(
                                    paymentFailedFormat.format(msg.orEmpty())
                                )
                                onNavigateBack()
                            }

                            override fun onPending() {
                                viewModel.resetState()
                                Log.d("PAYMOB", "onPending")

                                onPaymentFailure(paymentPendingText)
                                onNavigateBack()
                            }
                        }
                    ).build().start()
                } catch (e: Exception) {
                    viewModel.resetState()

                    onPaymentFailure(
                        paymentSdkErrorFormat.format(e.message.orEmpty())
                    )
                    onNavigateBack()
                }
            }

            is PaymentUiState.Error -> {
                viewModel.resetState()
                onPaymentFailure(current.message.resolve(context))
                onNavigateBack()
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}