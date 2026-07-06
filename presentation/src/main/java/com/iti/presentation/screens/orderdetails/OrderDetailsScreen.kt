package com.iti.presentation.screens.orderdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.domain.models.order.Order
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.screens.orderdetails.components.OrderDetailsList
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    order: Order,
    onNavigateBack: () -> Unit,
    onNavigateToSupport: () -> Unit,
    viewModel: OrderDetailsViewModel = koinViewModel(parameters = { parametersOf(order) })
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrderDetailsContract.Effect.NavigateToSupport -> onNavigateToSupport()
            }
        }
    }

    Scaffold(
        topBar = {
            OrdersTopBar(
                onNavigateBack = onNavigateBack,
                onNavigateToSupport = { viewModel.onEvent(OrderDetailsContract.Event.ContactSupportClicked) },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        OrderDetailsList(
            order = order,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersTopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigateBack: () -> Unit,
    onNavigateToSupport: () -> Unit,
) {
    BackTopBar(
        title = stringResource(R.string.order_details_title),
        onBack = onNavigateBack,
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = onNavigateToSupport
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = stringResource(R.string.order_details_support_cd),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}