package com.iti.presentation.screens.auth.emailverification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.iti.presentation.ui.theme.ShopIQTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmailVerificationScreen(
    onNavigateToSignIn: () -> Unit,
    viewModel: EmailVerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EmailVerificationContract.Effect.NavigateToSignIn -> {
                    onNavigateToSignIn()
                }

                is EmailVerificationContract.Effect.ShowInfo -> {
                    snackBarHostState.showSuccess(effect.message.resolve(context))
                }

                is EmailVerificationContract.Effect.ShowError -> {
                    snackBarHostState.showError(effect.message.resolve(context))
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { ShopIQSnackBarHost(hostState = snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader(
                title = stringResource(R.string.verify_your_email),
                subtitle = stringResource(R.string.verify_your_email_subtitle)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ShopIQButton(
                text = stringResource(R.string.send_verification_link),
                onClick = {
                    viewModel.onEvent(
                        EmailVerificationContract.Event.SendVerificationLink
                    )
                },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.onEvent(
                        EmailVerificationContract.Event.BackToLogin
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

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
private fun EmailVerificationScreenPreview() {
    ShopIQTheme {
        EmailVerificationScreen(
            onNavigateToSignIn = {}
        )
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
private fun EmailVerificationScreenDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        EmailVerificationScreen(
            onNavigateToSignIn = {}
        )
    }
}