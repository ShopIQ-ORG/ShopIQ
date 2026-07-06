package com.iti.presentation.screens.auth.signin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun SignInHeader(title: String, subtitle: String) {
    val isDark = LocalDarkTheme.current
    val logoRes = if (isDark) R.drawable.auth_logo_dark else R.drawable.auth_logo_light

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = stringResource(R.string.shopiq_logo),
        modifier = Modifier.height(72.dp)
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
private fun SignInHeaderPreview() {
    ShopIQTheme {
        SignInHeader(
            title = "Welcome Back",
            subtitle = "Login to your account"
        )
    }
}
