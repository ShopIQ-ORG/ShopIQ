package com.iti.presentation.screens.wishlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton

@Composable
fun EmptyWishlistState(
    textSecondaryColor: Color,
    buttonBgColor: Color,
    buttonTextColor: Color,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_wishlist),
            contentDescription = stringResource(id = R.string.wishlist_empty_title),
            modifier = Modifier.size(260.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.wishlist_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.wishlist_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondaryColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        ShopIQButton(
            text = stringResource(id = R.string.wishlist_explore_btn),
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}