package com.iti.presentation.screens.cart.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iti.domain.models.cart.CartItem
import com.iti.presentation.R
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.components.UnauthorizedContent
import com.iti.presentation.screens.cart.CartContract

@Composable
fun CartBody(
    state: CartContract.State,
    onCartItemClicked: (Long) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onBrowseProducts: () -> Unit,
    onLogin: () -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onTogglePromoExpanded: () -> Unit,
    onPromoInputChanged: (String) -> Unit,
    onApplyPromoClick: () -> Unit,
    onRemoveCoupon: (String) -> Unit,
    promoErrorMessage: String?,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        CartLoadingContent(modifier = modifier.fillMaxSize())
        return
    }

    if (state.accessRestricted) {
        UnauthorizedContent(
            title = stringResource(R.string.cart_unauthorized_title),
            message = stringResource(R.string.cart_unauthorized_message),
            onLogin = onLogin,
            modifier = modifier.fillMaxSize()
        )
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
        label = "cart_empty_transition"
    ) { isEmpty ->
        if (isEmpty) {
            if (state.isRefreshing) {
                CartLoadingContent(modifier = Modifier.fillMaxSize())
            } else {
                EmptyCartContent(
                    onBrowse = onBrowseProducts,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            val cart = state.cart!!
            CartContentList(
                cart = cart,
                onItemClicked = onCartItemClicked,
                isRefreshing = state.isRefreshing,
                itemBeingRemoved = state.itemBeingRemoved,
                isPromoExpanded = state.isPromoExpanded,
                promoInput = state.promoInput,
                isApplyingPromo = state.isApplyingPromo,
                removingCouponCode = state.removingCouponCode,
                promoErrorMessage = promoErrorMessage,
                onRefresh = onRefresh,
                onIncreaseQuantity = onIncreaseQuantity,
                onDecreaseQuantity = onDecreaseQuantity,
                onRemoveItem = onRemoveItem,
                onTogglePromoExpanded = onTogglePromoExpanded,
                onPromoInputChanged = onPromoInputChanged,
                onApplyPromoClick = onApplyPromoClick,
                onRemoveCoupon = onRemoveCoupon,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}