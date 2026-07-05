package com.iti.presentation.screens.orderdetails.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.screens.orderdetails.OrderDetailsContract

@Composable
fun OrderDetailsContent(
    state: OrderDetailsContract.State,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        OrderDetailsLoadingContent(modifier = modifier.fillMaxSize())
        return
    }

    if (state.error != null) {
        ErrorScreen(
            message = state.error,
            onRetry = onRetry,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val orderDetails = state.orderDetails ?: return

    OrderDetailsList(
        orderDetails = orderDetails,
        modifier = modifier.fillMaxSize()
    )
}