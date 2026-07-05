package com.iti.presentation.screens.address.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessContainerBorderDark
import com.iti.presentation.ui.theme.SuccessContainerBorderLight
import com.iti.presentation.ui.theme.SuccessContainerDark
import com.iti.presentation.ui.theme.SuccessContainerLight
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight

@Composable
fun SuccessBadge(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val containerColor = if (isDark) SuccessContainerDark else SuccessContainerLight
    val borderColor = if (isDark) SuccessContainerBorderDark else SuccessContainerBorderLight
    val successColor = if (isDark) SuccessDark else SuccessLight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = successColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.address_added_success_title),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = successColor
            )
            Text(
                text = stringResource(R.string.address_added_success_msg),
                style = MaterialTheme.typography.bodySmall,
                color = successColor.copy(alpha = 0.8f)
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.address_dismiss_desc),
                tint = successColor
            )
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun SuccessBadgeLightPreview() {
    ShopIQTheme(darkTheme = false) {
        SuccessBadge(onDismiss = {})
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun SuccessBadgeDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        SuccessBadge(onDismiss = {})
    }
}
