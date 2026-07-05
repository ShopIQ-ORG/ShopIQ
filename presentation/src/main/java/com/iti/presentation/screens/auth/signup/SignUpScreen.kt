package com.iti.presentation.screens.auth.signup

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
import com.iti.presentation.screens.auth.components.AuthFooter
import com.iti.presentation.screens.auth.components.AuthHeader
import com.iti.presentation.screens.auth.components.EmailField
import com.iti.presentation.screens.auth.components.FullNameField
import com.iti.presentation.screens.auth.components.PasswordField
import com.iti.presentation.screens.auth.components.PhoneField
import com.iti.presentation.screens.auth.components.TermsCheckbox
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.UiText
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
                    if (!effect.message.isFieldError()) {
                        snackBarHostState.showError(effect.message.resolve(context))
                    }
                }

                is SignUpContract.Effect.NavigateToEmailVerification -> onNavigateToEmailVerification(
                    effect.email
                )
            }
        }
    }

    val passwordMismatchError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_passwords_do_not_match
        }
        ?.resolve(context)

    val termsError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_agree_to_terms
        }
        ?.resolve(context)

    val fullNameRequiredError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_full_name_required
        }
        ?.resolve(context)

    val emailRequiredError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_email_required
        }
        ?.resolve(context)

    val phoneRequiredError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_phone_required
        }
        ?.resolve(context)

    val passwordRequiredError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_password_required
        }
        ?.resolve(context)

    val confirmPasswordRequiredError = state.error
        ?.takeIf {
            it is UiText.StringResource &&
                    it.resId == R.string.error_confirm_password_required
        }
        ?.resolve(context)

    Scaffold(
        snackbarHost = {
            ShopIQSnackBarHost(hostState = snackBarHostState)
        },
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
                    title = stringResource(R.string.create_account),
                    subtitle = stringResource(R.string.signup_subtitle)
                )

                Spacer(modifier = Modifier.height(24.dp))

                FullNameField(
                    value = state.fullName,
                    onValueChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.FullNameChanged(it)
                        )
                    },
                    errorMessage = fullNameRequiredError
                )

                Spacer(modifier = Modifier.height(16.dp))

                EmailField(
                    value = state.email,
                    onValueChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.EmailChanged(it)
                        )
                    },
                    errorMessage = emailRequiredError
                )

                Spacer(modifier = Modifier.height(16.dp))

                PhoneField(
                    value = state.phone,
                    onValueChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.PhoneChanged(it)
                        )
                    },
                    errorMessage = phoneRequiredError
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    value = state.password,
                    onValueChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.PasswordChanged(it)
                        )
                    },
                    errorMessage = passwordRequiredError
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    value = state.confirmPassword,
                    onValueChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.ConfirmPasswordChanged(it)
                        )
                    },
                    placeholder = stringResource(R.string.confirm_password),
                    errorMessage = passwordMismatchError ?: confirmPasswordRequiredError
                )

                Spacer(modifier = Modifier.height(16.dp))

                TermsCheckbox(
                    checked = state.agreeToTerms,
                    onCheckedChange = {
                        viewModel.onEvent(
                            SignUpContract.Event.AgreeToTermsChanged(it)
                        )
                    },
                    onTermsClick = {
                        viewModel.onEvent(
                            SignUpContract.Event.NavigateToTerms
                        )
                    },
                    onPrivacyClick = {
                        viewModel.onEvent(
                            SignUpContract.Event.NavigateToPrivacyPolicy
                        )
                    },
                    hasError = termsError != null
                )

                Spacer(modifier = Modifier.height(24.dp))

                ShopIQButton(
                    text = stringResource(R.string.create_account),
                    onClick = rememberSubmitAction {
                        viewModel.onEvent(
                            SignUpContract.Event.Register
                        )
                    },
                    isLoading = state.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthFooter(
                    text = stringResource(R.string.already_have_account),
                    clickableText = stringResource(R.string.login),
                    onClick = onNavigateToSignIn
                )
            }
        }
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