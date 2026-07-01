package com.iti.presentation.screens.cart

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.iti.domain.models.cart.CartItem
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.screens.cart.components.CartBody
import com.iti.presentation.screens.cart.components.CartCheckoutButton
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckout: () -> Unit,
    onCartItemClicked: (Long) -> Unit,
    onBrowseProducts: () -> Unit,
    onLogin: () -> Unit,
    viewModel: CartViewModel = koinViewModel()
) {
    val owner = LocalViewModelStoreOwner.current
    Log.d("CartScreen", owner.toString())

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var deleteDialogState by remember {
        mutableStateOf<CartItem?>(null)
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CartContract.Effect.NavigateToCheckout -> onCheckout()
                is CartContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.message.resolve(
                        context
                    )
                )

                is CartContract.Effect.ShowSuccess -> snackbarHostState.showSnackbar(
                    effect.message.resolve(
                        context
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = stringResource(R.string.cart_title),
                onBack = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            ShopIQSnackBarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            CartCheckoutButton(
                visible = state.canCheckout,
                onCheckoutClick = { viewModel.onEvent(CartContract.Event.ProceedToCheckout) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        CartBody(
            state = state,
            onCartItemClicked = onCartItemClicked,
            onRefresh = { viewModel.onEvent(CartContract.Event.Refresh) },
            onRetry = { viewModel.onEvent(CartContract.Event.Retry) },
            onBrowseProducts = onBrowseProducts,
            onLogin = onLogin,
            onIncreaseQuantity = { viewModel.onEvent(CartContract.Event.IncreaseQuantity(it)) },
            onDecreaseQuantity = { viewModel.onEvent(CartContract.Event.DecreaseQuantity(it)) },
            onRemoveItem = {
                deleteDialogState = it
            },
            onTogglePromoExpanded = { viewModel.onEvent(CartContract.Event.TogglePromoExpanded) },
            onPromoInputChanged = { viewModel.onEvent(CartContract.Event.PromoInputChanged(it)) },
            onApplyPromoClick = { viewModel.onEvent(CartContract.Event.ApplyPromoCode) },
            promoErrorMessage = state.promoError?.resolve(context),
            modifier = Modifier.padding(innerPadding)
        )
    }

    deleteDialogState?.let { item ->

        ConfirmationDialog(
            title = stringResource(R.string.remove_item),
            message = stringResource(
                R.string.remove_item_message,
                item.title
            ),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                viewModel.onEvent(CartContract.Event.RemoveItem(item.id))
            },
            onDismiss = {
            }
        )
    }
}