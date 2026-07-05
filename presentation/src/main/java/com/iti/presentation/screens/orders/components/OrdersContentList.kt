package com.iti.presentation.screens.orders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.Order
import com.iti.domain.models.order.*
import com.iti.presentation.ui.theme.ShopIQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContentList(
    orders: List<Order>,
    isRefreshing: Boolean,
    onOrderClicked: (Order) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onClick = { onOrderClicked(order) }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun OrdersContentListPreview() {
    val sampleOrders = listOf(
        previewOrder(
            id = "1",
            name = "#ORD-2024-1001",
            createdAt = "2024-05-20T10:30:00Z",
            total = 120.50,
            status = OrderStatus.PENDING,
            itemsCount = 3
        ),
        previewOrder(
            id = "2",
            name = "#ORD-2024-1002",
            createdAt = "2024-05-18T14:12:00Z",
            total = 64.00,
            status = OrderStatus.COMPLETED,
            itemsCount = 1
        ),
        previewOrder(
            id = "3",
            name = "#ORD-2024-1003",
            createdAt = "2024-05-15T09:05:00Z",
            total = 210.75,
            status = OrderStatus.CANCELLED,
            itemsCount = 5
        )
    )
    ShopIQTheme {
        OrdersContentList(
            orders = sampleOrders,
            isRefreshing = false,
            onOrderClicked = {},
            onRefresh = {}
        )
    }
}


private fun previewOrder(
    id: String,
    name: String,
    createdAt: String,
    total: Double,
    status: OrderStatus,
    itemsCount: Int,
    currencyCode: String = "USD"
) = Order(
    id = id,
    name = name,
    createdAt = createdAt,
    financialStatus = OrderFinancialStatus.PAID,
    fulfillmentStatus = status,
    subtotalPrice = Money(total, currencyCode),
    totalShippingPrice = Money(0.0, currencyCode),
    totalPrice = Money(total, currencyCode),
    totalRefunded = Money(0.0, currencyCode),
    totalTax = Money(0.0, currencyCode),
    shippingAddress = null,
    lineItems = listOf(
        OrderLineItem(
            title = "Sample Item",
            quantity = itemsCount,
            currentQuantity = itemsCount,
            originalTotalPrice = Money(total, currencyCode),
            discountedTotalPrice = Money(total, currencyCode),
            variant = null
        )
    )
)