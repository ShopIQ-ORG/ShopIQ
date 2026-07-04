package com.iti.presentation.screens.orderdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.OrderDetails

@Composable
fun OrderDetailsList(
    orderDetails: OrderDetails,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OrderInfoCard(orderDetails = orderDetails)
        }

        item {
            OrderStatusBanner(status = orderDetails.fulfillmentStatus)
        }

        item {
            OrderItemsSectionTitle()
        }

        items(items = orderDetails.lineItems, key = { it.title }) { lineItem ->
            OrderLineItemRow(lineItem = lineItem, currencyCode = orderDetails.currencyCode)
        }

        item {
            OrderSummaryCard(orderDetails = orderDetails)
        }

        orderDetails.shippingAddress?.let { shippingAddress ->
            item {
                ShippingAddressCard(shippingAddress = shippingAddress)
            }
        }
    }
}