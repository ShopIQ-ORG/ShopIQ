package com.iti.presentation.screens.auth.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQTextField

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.email_address),
    errorMessage: String? = null,
) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        errorMessage = errorMessage,
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.password),
    errorMessage: String? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Lock,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(R.string.toggle_password_visibility),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        errorMessage = errorMessage,
    )
}

@Composable
fun FullNameField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null,
) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.full_name),
        leadingIcon = Icons.Outlined.Person,
        errorMessage = errorMessage,
    )
}

@Composable
fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.phone_number),
    errorMessage: String? = null,
) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        errorMessage = errorMessage,
    )
}