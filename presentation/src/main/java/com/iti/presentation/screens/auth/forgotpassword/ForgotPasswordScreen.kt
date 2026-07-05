package com.iti.presentation.screens.auth.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.auth.components.AuthHeader
import com.iti.presentation.screens.auth.components.EmailField
import com.iti.presentation.ui.theme.ShopIQTheme
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

    val emailError = state.error?.resolve(context)

    Scaffold(
        snackbarHost = { ShopIQSnackBarHost(hostState = snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthHeader(
                    title = stringResource(R.string.forgot_password_title),
                    subtitle = stringResource(R.string.forgot_password_subtitle)
                )

                Spacer(modifier = Modifier.height(24.dp))

                EmailField(
                    value = state.email,
                    onValueChange = {
                        viewModel.onEvent(
                            ForgotPasswordContract.Event.EmailChanged(it)
                        )
                    },
                    placeholder = stringResource(R.string.email_address),
                    errorMessage = emailError
                )

                Spacer(modifier = Modifier.height(24.dp))

                ShopIQButton(
                    text = stringResource(R.string.send_reset_link),
                    onClick = {
                        viewModel.onEvent(
                            ForgotPasswordContract.Event.SendResetLink
                        )
                    },
                    isLoading = state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        viewModel.onEvent(
                            ForgotPasswordContract.Event.BackToLogin
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.back_to_login),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ShopIQTheme {
        ForgotPasswordScreen(
            onNavigateBack = {}
        )
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
private fun ForgotPasswordScreenDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        ForgotPasswordScreen(
            onNavigateBack = {}
        )
    }
}