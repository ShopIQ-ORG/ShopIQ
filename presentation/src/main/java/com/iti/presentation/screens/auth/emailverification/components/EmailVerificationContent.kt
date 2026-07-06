package com.iti.presentation.screens.auth.emailverification.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun EmailVerificationContent(
    email: String,
    resendCooldownSeconds: Int,
    isSendingLink: Boolean,
    isCheckingVerification: Boolean,
    onResendClick: () -> Unit,
    onContinueClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PulsingMailBadge()

        Spacer(modifier = Modifier.height(28.dp))

        VerificationHeader()

        Spacer(modifier = Modifier.height(16.dp))

        VerificationMessage(email = email)

        Spacer(modifier = Modifier.height(36.dp))

        ResendButton(
            cooldownSeconds = resendCooldownSeconds,
            isSendingLink = isSendingLink,
            onClick = onResendClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        ContinueButton(
            isCheckingVerification = isCheckingVerification,
            onClick = onContinueClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onBackToLoginClick) {
            Text(stringResource(R.string.back_to_sign_in))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailVerificationContentPreview() {
    ShopIQTheme {
        EmailVerificationContent(
            email = "user@example.com",
            resendCooldownSeconds = 30,
            isSendingLink = false,
            isCheckingVerification = false,
            onResendClick = {},
            onContinueClick = {},
            onBackToLoginClick = {}
        )
    }
}
