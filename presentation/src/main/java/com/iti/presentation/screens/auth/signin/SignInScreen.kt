package com.iti.presentation.screens.auth.signin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.core.UiText
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.showError
import com.iti.presentation.screens.auth.components.AuthFooter
import com.iti.presentation.screens.auth.components.AuthHeader
import com.iti.presentation.screens.auth.components.AuthSocialSection
import com.iti.presentation.screens.auth.components.EmailField
import com.iti.presentation.screens.auth.components.PasswordField
import com.iti.presentation.ui.theme.ShopIQTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.iti.presentation.screens.auth.rememberGoogleSignInHelper
import com.iti.presentation.screens.auth.rememberFacebookSignInHelper
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onSocialError: (String) -> Unit = { msg -> scope.launch { snackBarHostState.showError(msg) } }

    val googleHelper = rememberGoogleSignInHelper(
        onSuccess = { idToken -> viewModel.onIntent(SignInIntent.LoginWithGoogle(idToken)) },
        onError = onSocialError
    )

    val facebookHelper = rememberFacebookSignInHelper(
        onSuccess = { token -> viewModel.onIntent(SignInIntent.LoginWithFacebook(token)) },
        onError = onSocialError
    )


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignInEffect.NavigateToHome -> onNavigateToHome()
                is SignInEffect.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is SignInEffect.ShowError -> {
                    if (!effect.message.isFieldError()) {
                        snackBarHostState.showError(effect.message.resolve(context))
                    }
                }
            }
        }
    }

    val emailRequiredError = state.error
        ?.takeIf { it is UiText.StringResource && it.resId == R.string.error_email_required }
        ?.resolve(context)

    val passwordRequiredError = state.error
        ?.takeIf { it is UiText.StringResource && it.resId == R.string.error_password_required }
        ?.resolve(context)

    val otherFieldError = state.error
        ?.takeIf { it.isFieldError() && (it !is UiText.StringResource || (it.resId != R.string.error_email_required && it.resId != R.string.error_password_required)) }
        ?.resolve(context)

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
                    title = stringResource(R.string.welcome_back),
                    subtitle = stringResource(R.string.login_subtitle)
                )

                Spacer(modifier = Modifier.height(24.dp))

                EmailField(
                    value = state.email,
                    onValueChange = { viewModel.onIntent(SignInIntent.EmailChanged(it)) },
                    placeholder = stringResource(R.string.email_address),
                    errorMessage = otherFieldError ?: emailRequiredError,
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    value = state.password,
                    onValueChange = { viewModel.onIntent(SignInIntent.PasswordChanged(it)) },
                    errorMessage = otherFieldError ?: passwordRequiredError,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.onIntent(SignInIntent.ForgotPassword) }
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ShopIQButton(
                    text = stringResource(R.string.login),
                    onClick = { viewModel.onIntent(SignInIntent.Login) },
                    isLoading = state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthSocialSection(
                    onGoogleClick = { googleHelper.signIn() },
                    onFacebookClick = { facebookHelper.signIn() },
                    onGuestClick = { viewModel.onIntent(SignInIntent.LoginAsGuest) },
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthFooter(
                    text = stringResource(R.string.dont_have_account),
                    clickableText = stringResource(R.string.create_account),
                    onClick = onNavigateToSignUp
                )
            }
        }
    }
}

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
private fun SignInScreenPreview() {
    ShopIQTheme {
        SignInScreen(
            onNavigateToSignUp = {},
            onNavigateToHome = {},
            onNavigateToForgotPassword = {}
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
            onNavigateToForgotPassword = {}
        )
    }
}