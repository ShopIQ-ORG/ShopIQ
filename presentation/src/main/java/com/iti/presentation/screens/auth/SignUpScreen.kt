package com.iti.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.screens.auth.components.*

@Composable
fun SignUpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        AuthHeader(
            title = stringResource(R.string.create_account),
            subtitle = stringResource(R.string.signup_subtitle)
        )

        Spacer(modifier = Modifier.height(32.dp))

        FullNameField(
            value = fullName,
            onValueChange = { fullName = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        EmailField(
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PhoneField(
            value = phone,
            onValueChange = { phone = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = password,
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = stringResource(R.string.confirm_password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TermsCheckbox(
            checked = agreeToTerms,
            onCheckedChange = { agreeToTerms = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ShopIQButton(
            text = stringResource(R.string.create_account),
            onClick = onNavigateToHome
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthSocialSection()

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        AuthFooter(
            text = stringResource(R.string.already_have_account),
            clickableText = stringResource(R.string.login),
            onClick = onNavigateToSignIn
        )
    }
}
