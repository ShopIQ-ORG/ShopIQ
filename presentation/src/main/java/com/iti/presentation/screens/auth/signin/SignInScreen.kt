package com.iti.presentation.screens.auth.signin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.screens.auth.rememberGoogleSignInHelper
import com.iti.presentation.screens.auth.signin.components.SignInContent
import com.iti.presentation.screens.auth.signin.SignInContract
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.AuthField
import com.iti.presentation.util.rememberSubmitAction
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToEmailVerification: (String) -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onSocialError: (String) -> Unit = { message ->
        scope.launch {
            snackBarHostState.showError(message)
        }
    }

    val googleHelper = rememberGoogleSignInHelper(
        onSuccess = { idToken ->
            viewModel.onEvent(SignInContract.Event.LoginWithGoogle(idToken))
        },
        onError = onSocialError
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignInContract.Effect.NavigateToHome -> onNavigateToHome()
                SignInContract.Effect.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is SignInContract.Effect.ShowError -> {
                    snackBarHostState.showError(effect.message.resolve(context))
                }
                is SignInContract.Effect.NavigateToEmailVerification -> onNavigateToEmailVerification(effect.email)
            }
        }
    }

    Scaffold(
        snackbarHost = { ShopIQSnackBarHost(hostState = snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        SignInContent(
            modifier = Modifier.padding(innerPadding),
            emailValue = state.email,
            onEmailChange = { viewModel.onEvent(SignInContract.Event.EmailChanged(it)) },
            emailError = state.fieldErrors[AuthField.EMAIL]?.resolve(context),
            passwordValue = state.password,
            onPasswordChange = { viewModel.onEvent(SignInContract.Event.PasswordChanged(it)) },
            passwordError = state.fieldErrors[AuthField.PASSWORD]?.resolve(context),
            isLoading = state.isLoading,
            onForgotPasswordClick = { viewModel.onEvent(SignInContract.Event.ForgotPassword) },
            onLoginClick = rememberSubmitAction { viewModel.onEvent(SignInContract.Event.Login) },
            onGoogleClick = { googleHelper.signIn() },
            onGuestClick = { viewModel.onEvent(SignInContract.Event.LoginAsGuest) },
            onSignUpClick = onNavigateToSignUp
        )
    }
}

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    ShopIQTheme {
        SignInScreen(
            onNavigateToSignUp = {},
            onNavigateToHome = {},
            onNavigateToForgotPassword = {},
            onNavigateToEmailVerification = {}
        )
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
private fun SignInScreenDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        SignInScreen(
            onNavigateToSignUp = {},
            onNavigateToHome = {},
            onNavigateToForgotPassword = {},
            onNavigateToEmailVerification = {}
        )
    }
}