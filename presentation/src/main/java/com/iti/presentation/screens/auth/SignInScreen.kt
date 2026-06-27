package com.iti.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.screens.auth.components.*
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun SignInScreen(onNavigateToSignUp: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = stringResource(R.string.welcome_back),
                subtitle = stringResource(R.string.login_subtitle)
            )

            Spacer(modifier = Modifier.height(24.dp))

            EmailField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(R.string.email_or_phone_number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = password,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = stringResource(R.string.remember_me),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ShopIQButton(
                text = stringResource(R.string.login),
                onClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthSocialSection()

            Spacer(modifier = Modifier.height(32.dp))

            AuthFooter(
                text = stringResource(R.string.dont_have_account),
                clickableText = stringResource(R.string.create_account),
                onClick = onNavigateToSignUp
            )
        }
    }
}


@Preview(
    name = "Light Mode",
    showSystemUi = true
)
@Composable
private fun SignInScreenPreview() {
    ShopIQTheme {
        SignInScreen(
            onNavigateToSignUp = {}
        )
    }
}

@Preview(
    name = "Dark Mode",
    showSystemUi = true
)
@Composable
private fun SignInScreenDarkPreview() {
    ShopIQTheme(
        darkTheme = true
    ) {
        SignInScreen(
            onNavigateToSignUp = {}
        )
    }
}
