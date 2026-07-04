package com.iti.presentation.screens.cart.components.promocode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.R


@Composable
fun PromoInputRow(
    value: String,
    promoInput: String,
    isCodeApplied: Boolean,
    isApplying: Boolean,
    isRemoving: Boolean,
    onValueChange: (String) -> Unit,
    onApplyClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val isApplyEnabled = promoInput.isNotBlank()

    val applyColor by animateColorAsState(
        targetValue = if (isApplyEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "applyColor"
    )

    val applyAlpha by animateFloatAsState(
        targetValue = if (isApplyEnabled) 1f else 0.5f,
        label = "applyAlpha"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = !isCodeApplied,
        readOnly = isCodeApplied,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.cart_promo_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold
        ),
        trailingIcon = {
            when {
                isApplying || isRemoving -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(18.dp),
                        strokeWidth = 2.dp
                    )
                }

                isCodeApplied -> {
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cart_promo_remove_cd),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    Text(
                        text = stringResource(R.string.cart_promo_apply),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = applyColor,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .alpha(applyAlpha)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(
                                enabled = isApplyEnabled,
                                onClick = onApplyClick
                            )
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    )
}