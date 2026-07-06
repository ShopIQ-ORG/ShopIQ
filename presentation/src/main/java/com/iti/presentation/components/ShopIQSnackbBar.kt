package com.iti.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class ShopIQSnackbarType { Success, Error }

private data class ShopIQSnackbarVisuals(
    override val message: String,
    val type: ShopIQSnackbarType,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals

@Composable
fun ShopIQSnackBarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.imePadding()
    ) { data ->
        val visuals = data.visuals
        val type = (visuals as? ShopIQSnackbarVisuals)?.type ?: ShopIQSnackbarType.Error

        val containerColor = when (type) {
            ShopIQSnackbarType.Success -> MaterialTheme.colorScheme.primary
            ShopIQSnackbarType.Error -> MaterialTheme.colorScheme.error
        }
        val contentColor = when (type) {
            ShopIQSnackbarType.Success -> MaterialTheme.colorScheme.onPrimary
            ShopIQSnackbarType.Error -> MaterialTheme.colorScheme.onError
        }

        Snackbar(
            containerColor = containerColor,
            contentColor = contentColor,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (type) {
                        ShopIQSnackbarType.Success -> Icons.Filled.CheckCircle
                        ShopIQSnackbarType.Error -> Icons.Filled.Error
                    },
                    contentDescription = null,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = data.visuals.message)
            }
        }
    }
}

suspend fun SnackbarHostState.showError(message: String): SnackbarResult {
    return showSnackbar(
        ShopIQSnackbarVisuals(
            message = message,
            type = ShopIQSnackbarType.Error,
            duration = SnackbarDuration.Short
        )
    )
}

suspend fun SnackbarHostState.showSuccess(message: String): SnackbarResult {
    return showSnackbar(
        ShopIQSnackbarVisuals(
            message = message,
            type = ShopIQSnackbarType.Success,
            duration = SnackbarDuration.Short
        )
    )
}