package com.iti.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class ShopIQSnackbarType { Success, Error, Info }

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
    val currentData = hostState.currentSnackbarData
    var lastData by remember { mutableStateOf<SnackbarData?>(null) }
    if (currentData != null) {
        lastData = currentData
    }

    LaunchedEffect(currentData) {
        if (currentData != null) {
            delay(2200L)
            currentData.dismiss()
        }
    }

    AnimatedVisibility(
        visible = currentData != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(150)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(220)
        ) + fadeOut(animationSpec = tween(180)),
        modifier = modifier
            .imePadding()
            .padding(horizontal = 20.dp)
    ) {
        val data = lastData ?: return@AnimatedVisibility
        val visuals = data.visuals
        val type = (visuals as? ShopIQSnackbarVisuals)?.type ?: ShopIQSnackbarType.Error

        val baseColor = when (type) {
            ShopIQSnackbarType.Success -> MaterialTheme.colorScheme.primary
            ShopIQSnackbarType.Error -> MaterialTheme.colorScheme.error
            ShopIQSnackbarType.Info -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        val onBaseColor = when (type) {
            ShopIQSnackbarType.Success -> MaterialTheme.colorScheme.onPrimary
            ShopIQSnackbarType.Error -> MaterialTheme.colorScheme.onError
            ShopIQSnackbarType.Info -> MaterialTheme.colorScheme.surface
        }
        val surfaceColor = MaterialTheme.colorScheme.surface

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = surfaceColor.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = baseColor.copy(alpha = 0.35f)
            ),
            shadowElevation = 12.dp,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(baseColor.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (type) {
                            ShopIQSnackbarType.Success -> Icons.Filled.CheckCircle
                            ShopIQSnackbarType.Error -> Icons.Filled.Error
                            ShopIQSnackbarType.Info -> Icons.Filled.Info
                        },
                        contentDescription = null,
                        tint = baseColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = visuals.message,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                visuals.actionLabel?.let { label ->
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { data.performAction() }) {
                        Text(
                            text = label,
                            color = baseColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

suspend fun SnackbarHostState.showError(message: String, actionLabel: String? = null): SnackbarResult? {
    return showUnique(message = message, type = ShopIQSnackbarType.Error, actionLabel = actionLabel)
}

suspend fun SnackbarHostState.showSuccess(message: String, actionLabel: String? = null): SnackbarResult? {
    return showUnique(message = message, type = ShopIQSnackbarType.Success, actionLabel = actionLabel)
}

suspend fun SnackbarHostState.showInfo(message: String, actionLabel: String? = null): SnackbarResult? {
    return showUnique(message = message, type = ShopIQSnackbarType.Info, actionLabel = actionLabel)
}

private suspend fun SnackbarHostState.showUnique(
    message: String,
    type: ShopIQSnackbarType,
    actionLabel: String?
): SnackbarResult? {
    val current = currentSnackbarData?.visuals as? ShopIQSnackbarVisuals
    if (current != null && current.message == message && current.type == type) {
        return null
    }

    currentSnackbarData?.dismiss()

    return showSnackbar(
        ShopIQSnackbarVisuals(
            message = message,
            type = type,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short
        )
    )
}