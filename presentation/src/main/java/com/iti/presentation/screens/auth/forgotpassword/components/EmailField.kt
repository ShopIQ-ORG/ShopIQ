package com.iti.presentation.screens.auth.forgotpassword.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQTextField

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.email_address),
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        errorMessage = errorMessage,
        modifier = modifier.fillMaxWidth()
    )
}
