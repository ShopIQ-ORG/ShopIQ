package com.iti.presentation.screens.auth.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQTextField

@Composable
fun SignUpForm(
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ShopIQTextField(
            value = fullNameValue,
            onValueChange = onFullNameChange,
            placeholder = stringResource(R.string.full_name),
            leadingIcon = Icons.Outlined.Person,
            errorMessage = fullNameError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShopIQTextField(
            value = emailValue,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.email_address),
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorMessage = emailError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ShopIQTextField(
            value = phoneValue,
            onValueChange = onPhoneChange,
            placeholder = stringResource(R.string.phone_number),
            leadingIcon = Icons.Outlined.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            errorMessage = phoneError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        ShopIQTextField(
            value = passwordValue,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password),
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
            errorMessage = passwordError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        var confirmPasswordVisible by remember { mutableStateOf(false) }

        ShopIQTextField(
            value = confirmPasswordValue,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(R.string.confirm_password),
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = stringResource(R.string.toggle_password_visibility),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorMessage = confirmPasswordError,
        )
    }
}
