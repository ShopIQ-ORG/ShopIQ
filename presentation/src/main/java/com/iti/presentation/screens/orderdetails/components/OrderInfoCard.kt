package com.iti.presentation.screens.orderdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.Money
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.OrderFinancialStatus
import com.iti.domain.models.order.OrderStatus
import com.iti.presentation.R
import com.iti.presentation.screens.orders.components.OrderStatusStyle
import com.iti.presentation.screens.orders.components.previewOrder
import com.iti.presentation.screens.orders.components.toStyle
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.toDisplayDate

@Composable
fun OrderInfoCard(order: Order) {
    val style = order.fulfillmentStatus.toStyle()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(style.tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = style.icon, contentDescription = null, tint = style.tint)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.order_card_title, order.name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = order.createdAt.toDisplayDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(12.dp))

            OrderDetailsStatusChip(style = style)
        }
    }
}

@Composable
private fun OrderDetailsStatusChip(style: OrderStatusStyle) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(style.tint.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = style.label, style = MaterialTheme.typography.labelSmall, color = style.tint)
    }
}

@Composable
fun OrderStatusBanner(status: OrderStatus) {
    val style = status.toStyle()
    val content = status.toBannerContent()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(style.tint.copy(alpha = 0.10f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.tint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = content.title,
                style = MaterialTheme.typography.titleSmall,
                color = style.tint
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = content.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class OrderStatusBannerContent(val title: String, val message: String)

@Composable
private fun OrderStatus.toBannerContent(): OrderStatusBannerContent = when (this) {
    OrderStatus.PENDING -> OrderStatusBannerContent(
        title = stringResource(R.string.order_status_banner_pending_title),
        message = stringResource(R.string.order_status_banner_pending_message)
    )

    OrderStatus.PROCESSING -> OrderStatusBannerContent(
        title = stringResource(R.string.order_status_banner_processing_title),
        message = stringResource(R.string.order_status_banner_processing_message)
    )

    OrderStatus.COMPLETED -> OrderStatusBannerContent(
        title = stringResource(R.string.order_status_banner_completed_title),
        message = stringResource(R.string.order_status_banner_completed_message)
    )

    OrderStatus.CANCELLED -> OrderStatusBannerContent(
        title = stringResource(R.string.order_status_banner_cancelled_title),
        message = stringResource(R.string.order_status_banner_cancelled_message)
    )

    OrderStatus.UNKNOWN -> OrderStatusBannerContent(
        title = stringResource(R.string.order_status_banner_unknown_title),
        message = stringResource(R.string.order_status_banner_unknown_message)
    )
}

@Preview(showBackground = true, name = "Info Card - Pending")
@Composable
private fun OrderInfoCardPreview() {
    ShopIQTheme {
        OrderInfoCard(order = previewOrder())
    }
}

@Preview(showBackground = true, name = "Banner - Pending")
@Composable
private fun PendingBannerPreview() {
    ShopIQTheme {
        OrderStatusBanner(status = OrderStatus.PENDING)
    }
}

@Preview(showBackground = true, name = "Banner - Processing")
@Composable
private fun ProcessingBannerPreview() {
    ShopIQTheme {
        OrderStatusBanner(status = OrderStatus.PROCESSING)
    }
}

@Preview(showBackground = true, name = "Banner - Completed")
@Composable
private fun CompletedBannerPreview() {
    ShopIQTheme {
        OrderStatusBanner(status = OrderStatus.COMPLETED)
    }
}

@Preview(showBackground = true, name = "Banner - Cancelled")
@Composable
private fun CancelledBannerPreview() {
    ShopIQTheme {
        OrderStatusBanner(status = OrderStatus.CANCELLED)
    }
}
