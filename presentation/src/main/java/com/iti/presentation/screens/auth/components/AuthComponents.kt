package com.iti.presentation.screens.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.SocialLoginButton
import com.iti.presentation.ui.theme.LocalDarkTheme

@Composable
fun AuthHeader(title: String, subtitle: String) {
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

@Composable
fun AuthSocialSection(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
        SocialLoginButton(iconRes = R.drawable.ic_google, onClick = onGoogleClick, enabled = enabled)
        SocialLoginButton(iconRes = R.drawable.ic_guest, onClick = onGuestClick, enabled = enabled)
        SocialLoginButton(iconRes = R.drawable.ic_facebook, onClick = onFacebookClick, enabled = enabled)
    }
}

@Composable
fun AuthFooter(text: String, clickableText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = clickableText,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(8.dp)
        )
    }
}