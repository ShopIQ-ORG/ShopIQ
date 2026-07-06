package com.iti.presentation.screens.auth.emailverification.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQButtonStyle

@Composable
fun ContinueButton(
    isCheckingVerification: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ShopIQButton(
        text = stringResource(R.string.ive_verified_my_email),
        onClick = onClick,
        isLoading = isCheckingVerification,
        style = ShopIQButtonStyle.Secondary,
        modifier = modifier.fillMaxWidth()
    )
}
