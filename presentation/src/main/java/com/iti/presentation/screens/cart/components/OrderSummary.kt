package com.iti.presentation.screens.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.util.getLocalizedCode
import com.iti.domain.models.cart.Cart
import com.iti.presentation.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.iti.presentation.util.CurrencyManager

@Composable
fun OrderSummary(
    cart: Cart,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val currentCurrency by CurrencyManager.selectedCurrency.collectAsState()

        val convertedSubtotal = CurrencyManager.convertFromUsd(cart.subtotal.amount.toDoubleOrNull() ?: 0.0)
        val subtotalStr = if (convertedSubtotal % 1.0 == 0.0) "%.0f".format(convertedSubtotal) else "%.2f".format(convertedSubtotal)
        val context = androidx.compose.ui.platform.LocalContext.current
        SummaryRow(label = stringResource(R.string.cart_subtotal), value = "$subtotalStr ${currentCurrency.getLocalizedCode(context)}")

        val discount = cart.discountAmount
        if (discount != null && discount.amount.toDoubleOrNull() != 0.0) {
            val convertedDiscount = CurrencyManager.convertFromUsd(discount.amount.toDoubleOrNull() ?: 0.0)
            val discountStr = if (convertedDiscount % 1.0 == 0.0) "%.0f".format(convertedDiscount) else "%.2f".format(convertedDiscount)
            SummaryRow(
                label = stringResource(R.string.cart_discount),
                value = "-$discountStr ${currentCurrency.getLocalizedCode(androidx.compose.ui.platform.LocalContext.current)}",
                valueColor = MaterialTheme.colorScheme.tertiary
            )
        }

        val shipping = cart.shippingAmount
        val shippingValue = shipping?.amount?.toDoubleOrNull()

        val convertedShipping = if (shippingValue != null) CurrencyManager.convertFromUsd(shippingValue) else 0.0
        val shippingStr = if (convertedShipping % 1.0 == 0.0) "%.0f".format(convertedShipping) else "%.2f".format(convertedShipping)

        SummaryRow(
            label = stringResource(R.string.cart_shipping),
            value = when {
                shipping == null -> stringResource(R.string.cart_shipping_calculated_at_checkout)
                shippingValue == 0.0 -> stringResource(R.string.cart_shipping_free)
                else -> "$shippingStr ${currentCurrency.getLocalizedCode(androidx.compose.ui.platform.LocalContext.current)}"
            },
            valueColor = if (shippingValue == 0.0) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onBackground
            }
        )

        Spacer(Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.cart_total),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            val convertedTotal = CurrencyManager.convertFromUsd(cart.total.amount.toDoubleOrNull() ?: 0.0)
            val totalStr = if (convertedTotal % 1.0 == 0.0) "%.0f".format(convertedTotal) else "%.2f".format(convertedTotal)
            Text(
                text = "$totalStr ${currentCurrency.getLocalizedCode(androidx.compose.ui.platform.LocalContext.current)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}