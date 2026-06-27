package com.iti.presentation.screens.auth.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQTextField

@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit, placeholder: String = stringResource(R.string.email_address)) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit, placeholder: String = stringResource(R.string.password)) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        leadingIcon = Icons.Outlined.Lock,
        trailingIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(R.string.toggle_password_visibility),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun FullNameField(value: String, onValueChange: (String) -> Unit) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.full_name),
        leadingIcon = Icons.Outlined.Person
    )
}

@Composable
fun PhoneField(value: String, onValueChange: (String) -> Unit) {
    ShopIQTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = stringResource(R.string.phone_number_optional),
        leadingIcon = Icons.Outlined.Phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}
