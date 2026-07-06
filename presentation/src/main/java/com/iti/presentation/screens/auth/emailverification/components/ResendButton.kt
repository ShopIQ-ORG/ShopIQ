package com.iti.presentation.screens.auth.emailverification.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton

@Composable
fun ResendButton(
    cooldownSeconds: Int,
    isSendingLink: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ShopIQButton(
        text = if (cooldownSeconds > 0) {
            stringResource(R.string.resend_link_cooldown, cooldownSeconds)
        } else {
            stringResource(R.string.resend_verification_link)
        },
        onClick = onClick,
        isLoading = isSendingLink,
        enabled = cooldownSeconds == 0,
        modifier = modifier.fillMaxWidth()
    )
}
