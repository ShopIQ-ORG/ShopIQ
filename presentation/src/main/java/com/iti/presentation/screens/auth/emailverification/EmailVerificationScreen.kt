package com.iti.presentation.screens.auth.emailverification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.auth.emailverification.components.EmailVerificationContent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EmailVerificationScreen(
    email: String,
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: EmailVerificationViewModel = koinViewModel(parameters = { parametersOf(email) })
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    ObserveLifecycleForPolling(lifecycleOwner, viewModel)

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                EmailVerificationContract.Effect.NavigateToSignIn -> onNavigateToSignIn()
                EmailVerificationContract.Effect.NavigateToHome -> onNavigateToHome()
                is EmailVerificationContract.Effect.ShowInfo ->
                    snackbarHostState.showSuccess(effect.message.resolve(context))
                is EmailVerificationContract.Effect.ShowError ->
                    snackbarHostState.showError(effect.message.resolve(context))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { padding ->
            EmailVerificationContent(
                modifier = Modifier.padding(padding),
                email = state.email,
                resendCooldownSeconds = state.resendCooldownSeconds,
                isSendingLink = state.isSendingLink,
                isCheckingVerification = state.isCheckingVerification,
                onResendClick = {
                    viewModel.onEvent(EmailVerificationContract.Event.SendVerificationLink)
                },
                onContinueClick = {
                    viewModel.onEvent(EmailVerificationContract.Event.CheckVerification)
                },
                onBackToLoginClick = {
                    viewModel.onEvent(EmailVerificationContract.Event.BackToLogin)
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

@Composable
private fun ObserveLifecycleForPolling(
    lifecycleOwner: LifecycleOwner,
    viewModel: EmailVerificationViewModel
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startPolling()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopPolling()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}