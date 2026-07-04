package com.iti.presentation.screens.orders.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.screens.orders.OrdersContract

@Composable
fun OrdersContent(
    state: OrdersContract.State,
    onOrderClicked: (String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        OrdersLoadingContent(modifier = modifier.fillMaxSize())
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

    AnimatedContent(
        targetState = state.isEmpty,
        transitionSpec = {
            fadeIn(animationSpec = tween(250)) togetherWith
                    fadeOut(animationSpec = tween(200))
        },
        modifier = modifier.fillMaxSize(),
        label = "orders_empty_transition"
    ) { isEmpty ->
        if (isEmpty) {
            if (state.isRefreshing) {
                OrdersLoadingContent(modifier = Modifier.fillMaxSize())
            } else {
                EmptyOrdersContent(modifier = Modifier.fillMaxSize())
            }
        } else {
            OrdersContentList(
                orders = state.orders,
                isRefreshing = state.isRefreshing,
                onOrderClicked = onOrderClicked,
                onRefresh = onRefresh
            )
        }
    }
}