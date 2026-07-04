package com.iti.presentation.screens.orderdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.OrderDetails
import com.iti.domain.models.order.OrderStatus
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight
import com.iti.presentation.util.toCurrency

@Composable
fun OrderSummaryCard(orderDetails: OrderDetails) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.order_summary_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(14.dp))

            OrderSummaryRow(
                label = stringResource(R.string.order_summary_subtotal),
                value = orderDetails.subtotalPrice.toCurrency(orderDetails.currencyCode)
            )
            Spacer(Modifier.height(8.dp))
            OrderSummaryRow(
                label = stringResource(R.string.order_summary_shipping_fee),
                value = orderDetails.totalShippingPrice.toCurrency(orderDetails.currencyCode)
            )

            if (orderDetails.totalDiscounts > 0) {
                Spacer(Modifier.height(8.dp))
                val isDark = LocalDarkTheme.current
                OrderSummaryRow(
                    label = stringResource(R.string.order_summary_discount),
                    value = "-${orderDetails.totalDiscounts.toCurrency(orderDetails.currencyCode)}",
                    valueColor = if (isDark) SuccessDark else SuccessLight
                )
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(14.dp))

            OrderSummaryRow(
                label = stringResource(R.string.order_summary_total),
                value = orderDetails.totalPrice.toCurrency(orderDetails.currencyCode),
                labelWeight = FontWeight.SemiBold,
                valueWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun OrderSummaryRow(
    label: String,
    value: String,
    labelWeight: FontWeight = FontWeight.Normal,
    valueWeight: FontWeight = FontWeight.Medium,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = labelWeight,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = valueWeight,
            color = valueColor
        )
    }
}

@Preview(showBackground = true, name = "Summary - With Discount")
@Composable
private fun OrderSummaryCardWithDiscountPreview() {
    ShopIQTheme {
        OrderSummaryCard(
            orderDetails = previewOrderDetails(subtotal = 95.50, shipping = 10.0, discount = 5.0, total = 100.50)
        )
    }
}

@Preview(showBackground = true, name = "Summary - No Discount")
@Composable
private fun OrderSummaryCardNoDiscountPreview() {
    ShopIQTheme {
        OrderSummaryCard(
            orderDetails = previewOrderDetails(subtotal = 95.50, shipping = 10.0, discount = 0.0, total = 105.50)
        )
    }
}

private fun previewOrderDetails(
    subtotal: Double,
    shipping: Double,
    discount: Double,
    total: Double
) = OrderDetails(
    id = "1",
    name = "#ORD-2024-1001",
    createdAt = "2024-05-20T10:30:00Z",
    financialStatus = "paid",
    fulfillmentStatus = OrderStatus.PENDING,
    subtotalPrice = subtotal,
    totalShippingPrice = shipping,
    totalPrice = total,
    totalDiscounts = discount,
    currencyCode = "USD",
    shippingAddress = null,
    lineItems = emptyList()
)