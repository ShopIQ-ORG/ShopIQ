package com.iti.presentation.screens.cart.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton

@Composable
fun CartCheckoutBar(
    visible: Boolean,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    ShopIQButton(
        text = stringResource(R.string.cart_proceed_to_checkout),
        onClick = onCheckoutClick,
        leadingIcon = Icons.Rounded.ShoppingBag,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding()
    )
}