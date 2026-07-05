package com.iti.presentation.screens.orders.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.Money
import com.iti.presentation.R
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight
import com.iti.presentation.ui.theme.WarningDark
import com.iti.presentation.ui.theme.WarningLight
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.OrderFinancialStatus
import com.iti.domain.models.order.OrderStatus
import com.iti.presentation.util.toCurrency
import com.iti.presentation.util.toDisplayDate

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    val style = order.fulfillmentStatus.toStyle()

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OrderCardHeader(order = order, style = style)
            Spacer(Modifier.height(18.dp))
            OrderMetricsRow(order = order)
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))
            OrderCardFooter()
        }
    }
}

@Composable
private fun OrderCardHeader(order: Order, style: OrderStatusStyle) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(style.tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                tint = style.tint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.order_card_title, order.name),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = order.createdAt.toDisplayDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OrderStatusChip(style = style)
    }
}

@Composable
private fun OrderMetricsRow(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OrderMetricItem(
            icon = Icons.Default.Inventory2,
            label = pluralStringResource(R.plurals.order_items_count, order.itemsCount, order.itemsCount),
            value = stringResource(R.string.order_products_label)
        )

        VerticalDivider(
            modifier = Modifier.height(32.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        OrderMetricItem(
            icon = Icons.Default.CreditCard,
            label = order.totalPrice.toCurrency(),
            value = stringResource(R.string.order_total_label)
        )
    }
}

@Composable
private fun OrderMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = value, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun OrderCardFooter() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.order_view_details),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OrderStatusChip(style: OrderStatusStyle) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(style.tint.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = style.label,
            style = MaterialTheme.typography.labelSmall,
            color = style.tint
        )
    }
}

data class OrderStatusStyle(
    val label: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
fun OrderStatus.toStyle(): OrderStatusStyle {
    val isDark = LocalDarkTheme.current
    val success = if (isDark) SuccessDark else SuccessLight
    val warning = if (isDark) WarningDark else WarningLight

    return when (this) {
        OrderStatus.PENDING -> OrderStatusStyle(
            label = stringResource(R.string.order_status_pending),
            icon = Icons.Default.Inventory2,
            tint = warning
        )

        OrderStatus.PROCESSING -> OrderStatusStyle(
            label = stringResource(R.string.order_status_processing),
            icon = Icons.Default.Autorenew,
            tint = MaterialTheme.colorScheme.primary
        )

        OrderStatus.COMPLETED -> OrderStatusStyle(
            label = stringResource(R.string.order_status_completed),
            icon = Icons.Default.CheckCircle,
            tint = success
        )

        OrderStatus.CANCELLED -> OrderStatusStyle(
            label = stringResource(R.string.order_status_cancelled),
            icon = Icons.Default.Cancel,
            tint = MaterialTheme.colorScheme.error
        )

        OrderStatus.UNKNOWN -> OrderStatusStyle(
            label = stringResource(R.string.order_status_unknown),
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Pending")
@Composable
private fun PendingOrderCardPreview() {
    ShopIQTheme {
        OrderCard(order = previewOrder(status =OrderStatus.PENDING), onClick = {})
    }
}

@Preview(showBackground = true, name = "Processing")
@Composable
private fun ProcessingOrderCardPreview() {
    ShopIQTheme {
        OrderCard(order = previewOrder(status =OrderStatus.PROCESSING), onClick = {})
    }
}

@Preview(showBackground = true, name = "Completed")
@Composable
private fun CompletedOrderCardPreview() {
    ShopIQTheme {
        OrderCard(order = previewOrder(status =OrderStatus.COMPLETED), onClick = {})
    }
}

@Preview(showBackground = true, name = "Cancelled")
@Composable
private fun CancelledOrderCardPreview() {
    ShopIQTheme {
        OrderCard(order = previewOrder(status =OrderStatus.CANCELLED), onClick = {})
    }
}

@Preview(showBackground = true, name = "Dark - Completed", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkCompletedOrderCardPreview() {
    ShopIQTheme(darkTheme = true) {
        OrderCard(order = previewOrder(status = OrderStatus.COMPLETED), onClick = {})
    }
}

internal fun previewOrder(
    subtotal: Double = 95.50,
    shipping: Double = 10.0,
    tax: Double = 0.0,
    refunded: Double = 0.0,
    total: Double = 105.50,
    currencyCode: String = "USD",
    status: OrderStatus = OrderStatus.COMPLETED
) = Order(
    id = "1",
    name = "#ORD-2024-1001",
    createdAt = "2024-05-20T10:30:00Z",
    financialStatus = OrderFinancialStatus.PAID,
    fulfillmentStatus = OrderStatus.COMPLETED,
    subtotalPrice = Money(subtotal, currencyCode),
    totalShippingPrice = Money(shipping, currencyCode),
    totalPrice = Money(total, currencyCode),
    totalRefunded = Money(refunded, currencyCode),
    totalTax = Money(tax, currencyCode),
    shippingAddress = null,
    lineItems = emptyList()
)