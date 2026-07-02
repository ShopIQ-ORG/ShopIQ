package com.iti.presentation.screens.cart.components.cartitem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.iti.presentation.R

@Composable
fun LowStockWarning(
    quantityAvailable: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(
            R.string.cart_low_stock_warning,
            quantityAvailable
        ),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}