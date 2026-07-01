package com.iti.presentation.screens.cart.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.cart.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContentList(
    cart: Cart,
    isRefreshing: Boolean,
    onItemClicked: (Long) -> Unit,
    itemBeingRemoved: String?,
    isPromoExpanded: Boolean,
    promoInput: String,
    isApplyingPromo: Boolean,
    promoErrorMessage: String?,
    onRefresh: () -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onTogglePromoExpanded: () -> Unit,
    onPromoInputChanged: (String) -> Unit,
    onApplyPromoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.background,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                CartHeaderItem(
                    itemCount = cart.items.size,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            itemsIndexed(
                items = cart.items,
                key = { _, item -> item.id }
            ) { _, item ->
                CartItemRow(
                    item = item,
                    isBeingRemoved = itemBeingRemoved == item.id,
                    onIncrease = { onIncreaseQuantity(item.id) },
                    onDecrease = { onDecreaseQuantity(item.id) },
                    onRequestRemove = { onRemoveItem(item) },
                    onClick = onItemClicked,
                    modifier = Modifier.animateItem(
                        fadeOutSpec = tween(220),
                        placementSpec = tween(220)
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            item {
                PromoCodeSection(
                    isExpanded = isPromoExpanded,
                    promoInput = promoInput,
                    isApplying = isApplyingPromo,
                    errorMessage = promoErrorMessage,
                    onToggleExpand = onTogglePromoExpanded,
                    onInputChanged = onPromoInputChanged,
                    onApplyClick = onApplyPromoClick
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                OrderSummary(cart = cart)
            }
        }
    }
}