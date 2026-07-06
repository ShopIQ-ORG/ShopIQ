package com.iti.presentation.screens.auth.forgotpassword.components

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
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun ForgotPasswordContent(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    isLoading: Boolean,
    onSendResetLinkClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
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
            ForgotPasswordHeader(
                title = stringResource(R.string.forgot_password_title),
                subtitle = stringResource(R.string.forgot_password_subtitle)
            )

            Spacer(modifier = Modifier.height(24.dp))

            EmailField(
                value = emailValue,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.email_address),
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(24.dp))

            ResetButton(
                text = stringResource(R.string.send_reset_link),
                onClick = onSendResetLinkClick,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            BackToLoginButton(
                text = stringResource(R.string.back_to_login),
                onClick = onBackToLoginClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordContentPreview() {
    ShopIQTheme {
        ForgotPasswordContent(
            emailValue = "",
            onEmailChange = {},
            emailError = null,
            isLoading = false,
            onSendResetLinkClick = {},
            onBackToLoginClick = {}
        )
    }
}
