package com.iti.presentation.productdetails

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.iti.presentation.ui.theme.WarningLight
import com.iti.domain.models.Product
import com.iti.presentation.R
import com.iti.presentation.ui.theme.BackgroundDark
import com.iti.presentation.ui.theme.BackgroundLight
import com.iti.presentation.ui.theme.PrimaryLight
import com.iti.presentation.ui.theme.TextPrimaryDark
import com.iti.presentation.ui.theme.TextPrimaryLight
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Long = 9746399428843L, // Default fixed product ID
    viewModel: ProductDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Trigger initial load
    LaunchedEffect(productId) {
        viewModel.handleIntent(ProductDetailsIntent.LoadProductDetails(productId))
    }

    // Collect Side Effects
    LaunchedEffect(viewModel.sideEffects) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ProductDetailsSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_desc_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.handleIntent(ProductDetailsIntent.ToggleWishlist) }) {
                        Icon(
                            imageVector = if (state.isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.content_desc_wishlist),
                            tint = if (state.isWishlisted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = { /* Go to Cart */ }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = stringResource(id = R.string.content_desc_cart),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        // Badge count "3" as in mockup
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .size(16.dp)
                                .background(MaterialTheme.colorScheme.error, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                color = MaterialTheme.colorScheme.onError,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            state.product?.let {
                BottomActionBar(
                    isWishlisted = state.isWishlisted,
                    onWishlistToggle = { viewModel.handleIntent(ProductDetailsIntent.ToggleWishlist) },
                    onAddToCart = { viewModel.handleIntent(ProductDetailsIntent.AddToCart) }
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.error_message, state.error ?: ""),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.handleIntent(ProductDetailsIntent.LoadProductDetails(productId)) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(stringResource(id = R.string.btn_retry))
                        }
                    }
                }
            }
            state.product != null -> {
                val product = state.product!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Image Gallery
                    item {
                        ProductImageGallery(
                            images = product.images.map { it.url },
                            selectedIndex = state.selectedImageIndex,
                            onSelectIndex = { index ->
                                viewModel.handleIntent(ProductDetailsIntent.SelectImage(index))
                            }
                        )
                    }

                    // Title & Description Block
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Product Title
                            Text(
                                text = product.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            // Price & Rating Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${product.minPrice.currencyCode} ${product.minPrice.amount}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "★",
                                        color = WarningLight,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "4.6",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = " (" + stringResource(id = R.string.reviews_count, 128) + ")",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Description
                            Text(
                                text = product.description.ifEmpty { stringResource(id = R.string.no_description) },
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Color Selection
                    item {
                        ColorSelectionSection(
                            selectedColor = state.selectedColor ?: "Beige",
                            onColorSelect = { color ->
                                viewModel.handleIntent(ProductDetailsIntent.SelectColor(color))
                            }
                        )
                    }

                    // Size Selection
                    item {
                        SizeSelectionSection(
                            selectedSize = state.selectedSize ?: "M",
                            onSizeSelect = { size ->
                                viewModel.handleIntent(ProductDetailsIntent.SelectSize(size))
                            }
                        )
                    }

                    // Spacer bottom
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductImageGallery(
    images: List<String>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column of Thumbnails
        Column(
            modifier = Modifier
                .width(72.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val maxThumbnails = 3
            val displayCount = images.size.coerceAtMost(maxThumbnails)

            for (i in 0 until displayCount) {
                ThumbnailItem(
                    imageUrl = images[i],
                    isSelected = selectedIndex == i,
                    onClick = { onSelectIndex(i) }
                )
            }

            if (images.size > maxThumbnails) {
                val remaining = images.size - maxThumbnails
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .clickable { onSelectIndex(maxThumbnails) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$remaining",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Right Main Image Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (images.isNotEmpty()) {
                val currentImage = images.getOrNull(selectedIndex) ?: images.first()
                AsyncImage(
                    model = currentImage,
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback local drawable/placeholder if shopify image fails or is empty
                Image(
                    painter = painterResource(id = R.drawable.logo_light),
                    contentDescription = "Placeholder",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            }

            // Indicator Dots Row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                images.forEachIndexed { index, _ ->
                    val isSelected = index == selectedIndex
                    val width by animateDpAsState(targetValue = if (isSelected) 16.dp else 6.dp)
                    val color by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)

                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun ThumbnailItem(
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderStroke = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .border(borderStroke, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ColorSelectionSection(
    selectedColor: String,
    onColorSelect: (String) -> Unit
) {
    val colors = listOf(
        "Beige" to Color(0xFFE6D7C3),
        "Grey-Blue" to Color(0xFF8F9CA6),
        "Black" to Color(0xFF1A1D20)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.label_color, selectedColor),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            colors.forEach { (name, colorValue) ->
                val isSelected = name == selectedColor
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(if (isSelected) 3.dp else 0.dp)
                        .background(colorValue, CircleShape)
                        .clip(CircleShape)
                        .clickable { onColorSelect(name) }
                )
            }
        }
    }
}

@Composable
fun SizeSelectionSection(
    selectedSize: String,
    onSizeSelect: (String) -> Unit
) {
    val sizes = listOf("S", "M", "L", "XL", "XXL")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.label_size, selectedSize),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Show Size Guide */ }
            ) {
                // Size guide icon/text
                Text(
                    text = stringResource(id = R.string.size_guide),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sizes.forEach { size ->
                val isSelected = size == selectedSize
                val cardBorder = if (isSelected) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else {
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                }

                Box(
                    modifier = Modifier
                        .size(width = 54.dp, height = 44.dp)
                        .border(cardBorder, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable { onSizeSelect(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = size,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomActionBar(
    isWishlisted: Boolean,
    onWishlistToggle: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundLight
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add to Wishlist Button
            OutlinedButton(
                onClick = onWishlistToggle,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BackgroundLight,
                    contentColor = TextPrimaryLight
                )
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isWishlisted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.btn_wishlist),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BackgroundDark,
                    contentColor = TextPrimaryDark
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.btn_add_to_cart),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
