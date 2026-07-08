package com.iti.presentation.screens.orders

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.domain.models.order.Order
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.screens.orders.components.OrdersContent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (Order) -> Unit,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrdersContract.Effect.NavigateToOrderDetails -> onOrderClick(effect.order)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = stringResource(R.string.orders_title),
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        OrdersContent(
            state = state,
            onOrderClicked = { viewModel.onEvent(OrdersContract.Event.OrderClicked(it)) },
            onRefresh = { viewModel.onEvent(OrdersContract.Event.Refresh) },
            onRetry = { viewModel.onEvent(OrdersContract.Event.Retry) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}