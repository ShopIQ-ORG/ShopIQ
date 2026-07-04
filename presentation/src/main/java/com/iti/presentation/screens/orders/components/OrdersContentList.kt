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
import com.iti.domain.models.order.OrderStatus
import com.iti.presentation.ui.theme.ShopIQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContentList(
    orders: List<Order>,
    isRefreshing: Boolean,
    onOrderClicked: (String) -> Unit,
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
                    onClick = { onOrderClicked(order.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrdersContentListPreview() {
    val sampleOrders = listOf(
        Order("1", "#ORD-2024-1001", "2024-05-20T10:30:00Z", 120.50, "USD", OrderStatus.PENDING, 3),
        Order("2", "#ORD-2024-1002", "2024-05-18T14:12:00Z", 64.00, "USD", OrderStatus.COMPLETED, 1),
        Order("3", "#ORD-2024-1003", "2024-05-15T09:05:00Z", 210.75, "USD", OrderStatus.CANCELLED, 5)
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