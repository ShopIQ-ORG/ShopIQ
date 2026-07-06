package com.iti.presentation.screens.auth.signin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.SocialLoginButton
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun AuthSocialSection(
    onGoogleClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                text = stringResource(R.string.or_continue_with),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialLoginButton(
                iconRes = R.drawable.ic_google,
                text = stringResource(R.string.login_google),
                onClick = onGoogleClick,
                enabled = enabled,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            SocialLoginButton(
                iconRes = R.drawable.ic_guest,
                text = stringResource(R.string.login_guest),
                onClick = onGuestClick,
                enabled = enabled,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthSocialSectionPreview() {
    ShopIQTheme {
        AuthSocialSection(
            onGoogleClick = {},
            onGuestClick = {}
        )
    }
}
