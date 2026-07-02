package com.iti.presentation.screens.cart.components.cartitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Money
import com.iti.domain.models.cart.CartItem
import com.iti.presentation.R
import com.iti.presentation.ui.theme.ShopIQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemRow(
    item: CartItem,
    isBeingRemoved: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRequestRemove: () -> Unit,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val outOfStock = !item.isAvailableForSale

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled && !isBeingRemoved) {
                onRequestRemove()
            }
            false
        }
    )

    LaunchedEffect(isBeingRemoved) {
        if (!isBeingRemoved && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp)
                    .clickable(
                        enabled = !isBeingRemoved,
                        onClick = { onClick(item.productId.substringAfterLast("/").toLong()) }
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isBeingRemoved) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.cart_remove_item_cd),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CartItemImage(
                item = item,
                isBeingRemoved = isBeingRemoved,
                outOfStock = outOfStock
            )

            CartItemInfo(
                item = item,
                outOfStock = outOfStock,
                isBeingRemoved = isBeingRemoved,
                onIncrease = onIncrease,
                onDecrease = onDecrease,
                modifier = Modifier.weight(1f)
            )

            CartItemRemoveButton(
                isBeingRemoved = isBeingRemoved,
                onClick = onRequestRemove
            )
        }
    }
}


@Preview(showBackground = true, name = "Normal")
@Composable
private fun NormalPreview() {
    ShopIQTheme {
        CartItemRow(
            item = normalCartItem(),
            isBeingRemoved = false,
            onIncrease = {},
            onDecrease = {},
            onRequestRemove = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Low Stock")
@Composable
private fun LowStockPreview() {
    ShopIQTheme {
        CartItemRow(
            item = lowStockCartItem(),
            isBeingRemoved = false,
            onIncrease = {},
            onDecrease = {},
            onRequestRemove = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Out of Stock")
@Composable
private fun OutOfStockPreview() {
    ShopIQTheme {
        CartItemRow(
            item = outOfStockCartItem(),
            isBeingRemoved = false,
            onIncrease = {},
            onDecrease = {},
            onRequestRemove = {},
            onClick = {}
        )
    }
}



private fun cartItem(
    quantity: Int = 1,
    quantityAvailable: Int = 10,
    isAvailableForSale: Boolean = true
) = CartItem(
    id = "1",
    productId = "gid://shopify/Product/123456789",
    variantId = "gid://shopify/ProductVariant/987654321",
    title = "The Minimal Snowboard",
    variant = "Default Title",
    price = Money(
        amount = "885.95",
        currencyCode = "USD"
    ),
    imageUrl = "https://cdn.shopify.com/s/files/1/0838/0163/7099/files/snowboard_purple_hydrogen.png",
    quantity = quantity,
    isAvailableForSale = isAvailableForSale,
    quantityAvailable = quantityAvailable
)

private fun normalCartItem() = cartItem()

private fun lowStockCartItem() = cartItem(
    quantity = 2,
    quantityAvailable = 2
)

private fun outOfStockCartItem() = cartItem(
    quantity = 1,
    quantityAvailable = 0,
    isAvailableForSale = false
)



