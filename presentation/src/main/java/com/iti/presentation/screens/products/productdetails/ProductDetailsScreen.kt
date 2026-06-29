package com.iti.presentation.screens.products.productdetails

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.ui.theme.WarningLight
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Long = 9746399428843L,
    viewModel: ProductDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(productId) {
        viewModel.handleIntent(ProductDetailsIntent.LoadProductDetails(productId))
    }

    LaunchedEffect(viewModel.sideEffects) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ProductDetailsSideEffect.ShowToast ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = stringResource(id = R.string.product_details),
                onBack = onBackClick,
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(ProductDetailsIntent.ToggleWishlist) }) {
                        Icon(
                            imageVector = if (state.isWishlisted) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.content_desc_wishlist),
                            tint = if (state.isWishlisted) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> ProductDetailsShimmer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            state.error != null -> NoInternetScreen(
                onRetry = { viewModel.handleIntent(ProductDetailsIntent.LoadProductDetails(productId)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            state.product != null -> ProductDetailsContent(
                state = state,
                onIntent = viewModel::handleIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun ProductDetailsContent(
    state: ProductDetailsUiState,
    onIntent: (ProductDetailsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val product = state.product!!

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            item {
                when {
                    product.images.isEmpty() -> SingleProductImage(imageUrl = "",
                    )
                    product.images.size == 1 -> SingleProductImage(
                        imageUrl = product.images.first().url
                    )

                    else -> ProductImageGallery(
                        images = product.images.map { it.url },
                        selectedIndex = state.selectedImageIndex,
                        onSelectIndex = { onIntent(ProductDetailsIntent.SelectImage(it)) }
                    )
                }
            }

            item {
                ProductInfoBlock(
                    title = product.title,
                    currencyCode = product.minPrice.currencyCode,
                    amount = product.minPrice.amount,
                    description = product.description
                )
            }

            item {
                ColorSelectionSection(
                    selectedColor = state.selectedColor ?: "Beige",
                    onColorSelect = { onIntent(ProductDetailsIntent.SelectColor(it)) }
                )
            }

        }

        ShopIQButton(
            text = stringResource(id = R.string.btn_add_to_cart),
            onClick = { onIntent(ProductDetailsIntent.AddToCart) },
            leadingIcon = Icons.Rounded.ShoppingBag,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun SingleProductImage(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1f)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop,
        error = painterResource(id = R.drawable.logo_light)
    )
}

@Composable
private fun ProductImageGallery(
    images: List<String>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .width(64.dp)
                .aspectRatio(1f / 1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val maxThumbnails = 3
            val displayCount = images.size.coerceAtMost(maxThumbnails)

            repeat(displayCount) { i ->
                ThumbnailItem(
                    imageUrl = images[i],
                    isSelected = selectedIndex == i,
                    onClick = { onSelectIndex(i) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (images.size > maxThumbnails) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectIndex(maxThumbnails) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${images.size - maxThumbnails}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = images.getOrNull(selectedIndex) ?: images.first(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            ImageIndicatorDots(
                count = images.size,
                selectedIndex = selectedIndex,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
private fun ThumbnailItem(
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                ),
                RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ImageIndicatorDots(
    count: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val isSelected = index == selectedIndex
            val width by animateDpAsState(targetValue = if (isSelected) 14.dp else 4.dp)
            val color by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(width)
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
private fun ProductInfoBlock(
    title: String,
    currencyCode: String,
    amount: String,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 24.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currencyCode $amount",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(text = "★", color = WarningLight, fontSize = 13.sp)
                Text(
                    text = "4.6",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "(128 reviews)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = description.ifEmpty { stringResource(id = R.string.no_description) },
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ColorSelectionSection(
    selectedColor: String,
    onColorSelect: (String) -> Unit
) {
    val colors = listOf(
        "Beige" to Color(0xFFE6D7C3),
        "Grey-Blue" to Color(0xFF8F9CA6),
        "Black" to Color(0xFF1A1D20)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Color:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = selectedColor,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            colors.forEach { (name, colorValue) ->
                ColorSwatch(
                    color = colorValue,
                    isSelected = name == selectedColor,
                    onClick = { onColorSelect(name) }
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (isSelected) 30.dp else 28.dp)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .padding(if (isSelected) 3.dp else 0.dp)
            .background(color, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    )
}