package com.iti.presentation.screens.cart.components.promocode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun PromoCodeSection(
    isExpanded: Boolean,
    appliedCode: String?,
    promoInput: String,
    isApplying: Boolean,
    isRemoving: Boolean,
    errorMessage: String?,
    onToggleExpand: () -> Unit,
    onInputChanged: (String) -> Unit,
    onApplyClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "promo_rotation"
    )

    val isCodeApplied = appliedCode != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {

        PromoHeader(
            isCodeApplied = isCodeApplied,
            rotation = rotation,
            onClick = onToggleExpand
        )

        AnimatedVisibility(isExpanded) {
            Column(
                modifier = Modifier.padding(top = 12.dp)
            ) {

                PromoInputRow(
                    value = appliedCode ?: promoInput,
                    promoInput = promoInput,
                    isCodeApplied = isCodeApplied,
                    isApplying = isApplying,
                    isRemoving = isRemoving,
                    onValueChange = onInputChanged,
                    onApplyClick = onApplyClick,
                    onRemoveClick = onRemoveClick
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Collapsed",
    showBackground = true
)
@Composable
private fun PromoCodeSectionCollapsedPreview() {
    ShopIQTheme {
        PromoCodeSection(
            isExpanded = false,
            appliedCode = null,
            promoInput = "",
            isApplying = false,
            isRemoving = false,
            errorMessage = null,
            onToggleExpand = {},
            onInputChanged = {},
            onApplyClick = {},
            onRemoveClick = {}
        )
    }
}

@Preview(
    name = "Expanded",
    showBackground = true
)
@Composable
private fun PromoCodeSectionExpandedPreview() {
    ShopIQTheme {
        PromoCodeSection(
            isExpanded = true,
            appliedCode = null,
            promoInput = "SUMMER25",
            isApplying = false,
            isRemoving = false,
            errorMessage = null,
            onToggleExpand = {},
            onInputChanged = {},
            onApplyClick = {},
            onRemoveClick = {}
        )
    }
}

@Preview(
    name = "Applied",
    showBackground = true
)
@Composable
private fun PromoCodeSectionAppliedPreview() {
    ShopIQTheme {
        PromoCodeSection(
            isExpanded = true,
            appliedCode = "SUMMER25",
            promoInput = "",
            isApplying = false,
            isRemoving = false,
            errorMessage = null,
            onToggleExpand = {},
            onInputChanged = {},
            onApplyClick = {},
            onRemoveClick = {}
        )
    }
}