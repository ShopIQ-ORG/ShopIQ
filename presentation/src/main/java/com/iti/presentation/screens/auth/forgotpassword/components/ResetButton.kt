package com.iti.presentation.screens.auth.forgotpassword.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.presentation.components.ShopIQButton

@Composable
fun ResetButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    ShopIQButton(
        text = text,
        onClick = onClick,
        isLoading = isLoading,
        modifier = modifier.fillMaxWidth()
    )
}
