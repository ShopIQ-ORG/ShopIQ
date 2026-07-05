package com.iti.presentation.screens.address.components

import android.R.id.message
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessContainerBorderDark
import com.iti.presentation.ui.theme.SuccessContainerBorderLight
import com.iti.presentation.ui.theme.SuccessContainerDark
import com.iti.presentation.ui.theme.SuccessContainerLight
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight
import kotlinx.coroutines.delay

@Composable
fun TopSnackbar(
    modifier: Modifier = Modifier,
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    isError: Boolean = false,
) {
    val isDark = LocalDarkTheme.current
    
    val containerColor = if (isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        if (isDark) SuccessContainerDark else SuccessContainerLight
    }
    
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        if (isDark) SuccessContainerBorderDark else SuccessContainerBorderLight
    }
    
    val contentColor = if (isError) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        if (isDark) SuccessDark else SuccessLight
    }

    LaunchedEffect(visible) {
        if (visible) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColor)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = contentColor
                    )
                }
            }
        }
    }
}

@Preview(name = "Success Mode")
@Composable
private fun TopSnackbarSuccessPreview() {
    ShopIQTheme(darkTheme = false) {
        TopSnackbar(
            message = "Address added successfully.",
            visible = true,
            onDismiss = {}
        )
    }
}

@Preview(name = "Error Mode")
@Composable
private fun TopSnackbarErrorPreview() {
    ShopIQTheme(darkTheme = false) {
        TopSnackbar(
            message = "Please select a location on the map first.",
            visible = true,
            onDismiss = {},
            isError = true
        )
    }
}
