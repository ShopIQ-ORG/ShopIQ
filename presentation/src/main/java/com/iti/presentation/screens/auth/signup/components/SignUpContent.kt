package com.iti.presentation.screens.auth.signup.components

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
fun SignUpContent(
    fullNameValue: String,
    onFullNameChange: (String) -> Unit,
    fullNameError: String?,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    phoneValue: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String?,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    passwordError: String?,
    confirmPasswordValue: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordError: String?,
    agreeToTerms: Boolean,
    onAgreeToTermsChange: (Boolean) -> Unit,
    termsError: Boolean,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    isLoading: Boolean,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
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
            SignUpHeader(
                title = stringResource(R.string.create_account),
                subtitle = stringResource(R.string.signup_subtitle)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignUpForm(
                fullNameValue = fullNameValue,
                onFullNameChange = onFullNameChange,
                fullNameError = fullNameError,
                emailValue = emailValue,
                onEmailChange = onEmailChange,
                emailError = emailError,
                phoneValue = phoneValue,
                onPhoneChange = onPhoneChange,
                phoneError = phoneError,
                passwordValue = passwordValue,
                onPasswordChange = onPasswordChange,
                passwordError = passwordError,
                confirmPasswordValue = confirmPasswordValue,
                onConfirmPasswordChange = onConfirmPasswordChange,
                confirmPasswordError = confirmPasswordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            TermsSection(
                checked = agreeToTerms,
                onCheckedChange = onAgreeToTermsChange,
                onTermsClick = onTermsClick,
                onPrivacyClick = onPrivacyClick,
                hasError = termsError
            )

            Spacer(modifier = Modifier.height(24.dp))

            ShopIQButton(
                text = stringResource(R.string.create_account),
                onClick = onSignUpClick,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignUpFooter(
                onSignInClick = onSignInClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpContentPreview() {
    ShopIQTheme {
        SignUpContent(
            fullNameValue = "",
            onFullNameChange = {},
            fullNameError = null,
            emailValue = "",
            onEmailChange = {},
            emailError = null,
            phoneValue = "",
            onPhoneChange = {},
            phoneError = null,
            passwordValue = "",
            onPasswordChange = {},
            passwordError = null,
            confirmPasswordValue = "",
            onConfirmPasswordChange = {},
            confirmPasswordError = null,
            agreeToTerms = false,
            onAgreeToTermsChange = {},
            termsError = false,
            onTermsClick = {},
            onPrivacyClick = {},
            isLoading = false,
            onSignUpClick = {},
            onSignInClick = {}
        )
    }
}
