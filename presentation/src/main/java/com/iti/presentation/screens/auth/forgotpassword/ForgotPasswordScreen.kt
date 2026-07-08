package com.iti.presentation.screens.auth.forgotpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.auth.forgotpassword.components.ForgotPasswordContent
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.AuthField
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ForgotPasswordContract.Effect.NavigateBack -> onNavigateBack()
                is ForgotPasswordContract.Effect.ShowSuccess -> {
                    snackBarHostState.showSuccess(effect.message.resolve(context))
                }
                is ForgotPasswordContract.Effect.ShowError -> {
                    snackBarHostState.showError(effect.message.resolve(context))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            ForgotPasswordContent(
                modifier = Modifier.padding(innerPadding),
                emailValue = state.email,
                onEmailChange = { viewModel.onEvent(ForgotPasswordContract.Event.EmailChanged(it)) },
                emailError = state.fieldErrors[AuthField.EMAIL]?.resolve(context),
                isLoading = state.isLoading,
                onSendResetLinkClick = { viewModel.onEvent(ForgotPasswordContract.Event.SendResetLink) },
                onBackToLoginClick = { viewModel.onEvent(ForgotPasswordContract.Event.BackToLogin) }
            )
        }

        ShopIQSnackBarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }
}

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ShopIQTheme {
        ForgotPasswordScreen(onNavigateBack = {})
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
private fun ForgotPasswordScreenDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        ForgotPasswordScreen(onNavigateBack = {})
    }
}