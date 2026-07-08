package com.iti.presentation.screens.cart.components.cartitem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.domain.models.cart.CartItem
import com.iti.domain.models.cart.atMaxQuantity
import com.iti.domain.models.cart.isLowStock
import com.iti.presentation.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.iti.presentation.util.CurrencyManager
import com.iti.presentation.util.getLocalizedCode

@Composable
fun CartItemInfo(
    item: CartItem,
    outOfStock: Boolean,
    isBeingRemoved: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.alpha(if (isBeingRemoved) 0.5f else 1f),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1
        )

        Text(
            text = item.variant,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (outOfStock) {
            Text(
                text = stringResource(R.string.cart_out_of_stock),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )
        }

        val currentCurrency by CurrencyManager.selectedCurrency.collectAsState()
        val convertedPrice = CurrencyManager.convertFromUsd(item.price.amount.toDoubleOrNull() ?: 0.0)
        val priceStr = if (convertedPrice % 1.0 == 0.0) "%.0f".format(convertedPrice) else "%.2f".format(convertedPrice)
        Text(
            text = "$priceStr ${currentCurrency.getLocalizedCode(androidx.compose.ui.platform.LocalContext.current)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )

        QuantitySelector(
            quantity = item.quantity,
            onIncrease = onIncrease,
            onDecrease = onDecrease,
            enabled = !isBeingRemoved && !outOfStock,
            canIncrease = !outOfStock && !item.atMaxQuantity
        )

        AnimatedVisibility(
            visible = item.isLowStock,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LowStockWarning(
                quantityAvailable = item.quantityAvailable,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}