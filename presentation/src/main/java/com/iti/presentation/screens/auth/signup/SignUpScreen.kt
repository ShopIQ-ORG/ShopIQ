package com.iti.presentation.screens.auth.signup

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
import com.iti.presentation.screens.auth.signup.SignUpContract
import com.iti.presentation.screens.auth.signup.components.SignUpContent
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.AuthField
import com.iti.presentation.util.rememberSubmitAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToEmailVerification: (String) -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpContract.Effect.NavigateToHome -> onNavigateToHome()
                SignUpContract.Effect.NavigateToTerms -> {}
                SignUpContract.Effect.NavigateToPrivacyPolicy -> {}
                is SignUpContract.Effect.ShowError -> {
                    snackBarHostState.showError(effect.message.resolve(context))
                }
                is SignUpContract.Effect.NavigateToEmailVerification -> onNavigateToEmailVerification(effect.email)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            SignUpContent(
                modifier = Modifier.padding(innerPadding),
                fullNameValue = state.fullName,
                onFullNameChange = { viewModel.onEvent(SignUpContract.Event.FullNameChanged(it)) },
                fullNameError = state.fieldErrors[AuthField.FULL_NAME]?.resolve(context),
                emailValue = state.email,
                onEmailChange = { viewModel.onEvent(SignUpContract.Event.EmailChanged(it)) },
                emailError = state.fieldErrors[AuthField.EMAIL]?.resolve(context),
                phoneValue = state.phone,
                onPhoneChange = { viewModel.onEvent(SignUpContract.Event.PhoneChanged(it)) },
                phoneError = state.fieldErrors[AuthField.PHONE]?.resolve(context),
                passwordValue = state.password,
                onPasswordChange = { viewModel.onEvent(SignUpContract.Event.PasswordChanged(it)) },
                passwordError = state.fieldErrors[AuthField.PASSWORD]?.resolve(context),
                confirmPasswordValue = state.confirmPassword,
                onConfirmPasswordChange = { viewModel.onEvent(SignUpContract.Event.ConfirmPasswordChanged(it)) },
                confirmPasswordError = state.fieldErrors[AuthField.CONFIRM_PASSWORD]?.resolve(context),
                agreeToTerms = state.agreeToTerms,
                onAgreeToTermsChange = { viewModel.onEvent(SignUpContract.Event.AgreeToTermsChanged(it)) },
                termsError = state.fieldErrors.containsKey(AuthField.TERMS),
                onTermsClick = { viewModel.onEvent(SignUpContract.Event.NavigateToTerms) },
                onPrivacyClick = { viewModel.onEvent(SignUpContract.Event.NavigateToPrivacyPolicy) },
                isLoading = state.isLoading,
                onSignUpClick = rememberSubmitAction { viewModel.onEvent(SignUpContract.Event.Register) },
                onSignInClick = onNavigateToSignIn
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
private fun SignUpScreenPreview() {
    ShopIQTheme {
        SignUpScreen(
            onNavigateToHome = {},
            onNavigateToSignIn = {},
            onNavigateToEmailVerification = {}
        )
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
private fun SignUpScreenDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        SignUpScreen(
            onNavigateToHome = {},
            onNavigateToSignIn = {},
            onNavigateToEmailVerification = {}
        )
    }
}