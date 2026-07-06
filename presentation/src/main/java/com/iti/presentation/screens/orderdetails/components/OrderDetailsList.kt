package com.iti.presentation.screens.orderdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.domain.models.order.Order

@Composable
fun OrderDetailsList(
    order: Order,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OrderInfoCard(order = order)
        }

        item {
            OrderStatusBanner(status = order.fulfillmentStatus)
        }

        item {
            OrderItemsSectionTitle()
        }

        itemsIndexed(
            items = order.lineItems,
            key = { index, lineItem -> lineItem.variant?.id ?: index }
        ) { _, lineItem ->
            OrderLineItemRow(lineItem = lineItem)
        }

        item {
            OrderSummaryCard(order = order)
        }

        order.shippingAddress?.let { shippingAddress ->
            item {
                ShippingAddressCard(shippingAddress = shippingAddress)
            }
        }
    }
}