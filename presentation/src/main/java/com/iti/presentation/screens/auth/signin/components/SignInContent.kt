package com.iti.presentation.screens.auth.signin.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun SignInContent(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    isLoading: Boolean,
    onForgotPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onGuestClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
            SignInHeader(
                title = stringResource(R.string.welcome_back),
                subtitle = stringResource(R.string.login_subtitle)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignInForm(
                emailValue = emailValue,
                onEmailChange = onEmailChange,
                emailError = emailError,
                passwordValue = passwordValue,
                onPasswordChange = onPasswordChange,
                passwordError = passwordError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ForgotPasswordText(onClick = onForgotPasswordClick)

            Spacer(modifier = Modifier.height(16.dp))

            ShopIQButton(
                text = stringResource(R.string.login),
                onClick = onLoginClick,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthSocialSection(
                onGoogleClick = onGoogleClick,
                onGuestClick = onGuestClick,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            SignInFooter(
                onSignUpClick = onSignUpClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInContentPreview() {
    ShopIQTheme {
        SignInContent(
            emailValue = "",
            onEmailChange = {},
            emailError = null,
            passwordValue = "",
            onPasswordChange = {},
            passwordError = null,
            isLoading = false,
            onForgotPasswordClick = {},
            onLoginClick = {},
            onGoogleClick = {},
            onGuestClick = {},
            onSignUpClick = {}
        )
    }
}
