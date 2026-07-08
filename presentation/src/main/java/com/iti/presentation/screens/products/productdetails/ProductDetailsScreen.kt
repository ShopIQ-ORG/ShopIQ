package com.iti.presentation.screens.products.productdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Money
import com.iti.domain.models.Product
import com.iti.domain.models.ProductImage
import com.iti.presentation.R
import com.iti.presentation.util.CurrencyManager
import com.iti.presentation.util.compareAtPrice
import com.iti.presentation.util.discountPercent
import com.iti.presentation.util.getLocalizedCode
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.ConfirmationDialog
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.components.ShopIQSnackBarHost
import com.iti.presentation.components.UnauthorizedDialog
import com.iti.presentation.components.showError
import com.iti.presentation.components.showSuccess
import com.iti.presentation.screens.products.productdetails.components.ColorSelectionSection
import com.iti.presentation.screens.products.productdetails.components.ProductImageGallery
import com.iti.presentation.screens.products.productdetails.components.ProductInfoBlock
import com.iti.presentation.screens.products.productdetails.components.SingleProductImage
import com.iti.presentation.screens.products.productdetails.components.RatingSummaryBlock
import com.iti.presentation.screens.products.productdetails.components.ReviewsListBlock
import com.iti.presentation.ui.theme.ShopIQTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: Long = 9746399428843L,
    viewModel: ProductDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onLogin: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showRemoveFavoriteConfirmation by remember { mutableStateOf(false) }

    var showAddReviewDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var reviewToEdit by remember { androidx.compose.runtime.mutableStateOf<com.iti.domain.models.ProductReview?>(null) }
    var animateIn by remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.product != null) {
            animateIn = true
        }
    }

    LaunchedEffect(state.isSubmittingReview) {
        if (!state.isSubmittingReview && state.reviewError == null) {
            showAddReviewDialog = false
            reviewToEdit = null
        }
    }

    LaunchedEffect(productId) {
        viewModel.handleIntent(ProductDetailsIntent.LoadProductDetails(productId))
    }

    LaunchedEffect(viewModel.sideEffects) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is ProductDetailsSideEffect.ShowSnackbar -> {
                    scope.launch {
                        val label = effect.actionLabel?.resolve(context)
                        val result = if (effect.kind == SnackbarKind.Success) {
                            snackbarHostState.showSuccess(effect.message.resolve(context), label)
                        } else {
                            snackbarHostState.showError(effect.message.resolve(context), label)
                        }
                        if (result == SnackbarResult.ActionPerformed && effect.isCartAction) {
                            onNavigateToCart()
                        }
                    }
                }
                ProductDetailsSideEffect.NavigateToAuth -> onLogin()
            }
        }
    }

    if (state.showUnauthorizedDialog) {
        UnauthorizedDialog(
            onDismiss = { viewModel.handleIntent(ProductDetailsIntent.DismissUnauthorizedDialog) },
            onLogin = {
                viewModel.handleIntent(ProductDetailsIntent.DismissUnauthorizedDialog)
                onLogin()
            }
        )
    }

    if (showAddReviewDialog) {
        com.iti.presentation.screens.products.productdetails.components.AddReviewDialog(
            onDismiss = { 
                showAddReviewDialog = false
                reviewToEdit = null
            },
            onSubmit = { rating, title, body ->
                val editReview = reviewToEdit
                if (editReview != null) {
                    viewModel.handleIntent(ProductDetailsIntent.EditReview(editReview.id, rating, title, body))
                } else {
                    viewModel.handleIntent(ProductDetailsIntent.SubmitReview(rating, title, body))
                }
            },
            isSubmitting = state.isSubmittingReview,
            error = state.reviewError,
            initialRating = reviewToEdit?.rating ?: 5,
            initialTitle = reviewToEdit?.title ?: "",
            initialBody = reviewToEdit?.body ?: ""
        )
    }

    if (showRemoveFavoriteConfirmation) {
        ConfirmationDialog(
            title = stringResource(id = R.string.remove_favorite_title),
            message = stringResource(id = R.string.remove_favorite_message),
            confirmText = stringResource(id = R.string.remove_favorite_confirm),
            dismissText = stringResource(id = R.string.remove_favorite_cancel),
            onConfirm = {
                showRemoveFavoriteConfirmation = false
                viewModel.handleIntent(ProductDetailsIntent.ToggleWishlist)
            },
            onDismiss = { showRemoveFavoriteConfirmation = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                BackTopBar(
                    title = stringResource(id = R.string.product_details),
                    onBack = onBackClick,
                    actions = {
                        IconButton(
                            onClick = {
                                if (state.isWishlisted) {
                                    showRemoveFavoriteConfirmation = true
                                } else {
                                    viewModel.handleIntent(ProductDetailsIntent.ToggleWishlist)
                                }
                            }
                        ) {
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

                state.product != null -> {
                    AnimatedVisibility(
                        visible = animateIn,
                        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                            initialOffsetY = { it / 6 },
                            animationSpec = tween(400)
                        )
                    ) {
                        ProductDetailsContent(
                            state = state,
                            onIntent = viewModel::handleIntent,
                            onWriteReviewClick = { showAddReviewDialog = true },
                            onEditReviewClick = { review ->
                                reviewToEdit = review
                                showAddReviewDialog = true
                            },
                            onDeleteReviewClick = { review ->
                                viewModel.handleIntent(ProductDetailsIntent.DeleteReview(review.id))
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }

        ShopIQSnackBarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun ProductDetailsContent(
    state: ProductDetailsUiState,
    onIntent: (ProductDetailsIntent) -> Unit,
    onWriteReviewClick: () -> Unit,
    onEditReviewClick: (com.iti.domain.models.ProductReview) -> Unit,
    onDeleteReviewClick: (com.iti.domain.models.ProductReview) -> Unit,
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
                    product.images.isEmpty() -> SingleProductImage(imageUrl = "")
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
                val currentCurrency by CurrencyManager.selectedCurrency.collectAsState()
                val currencyLabel = currentCurrency.getLocalizedCode(LocalContext.current)

                val convertedMinPrice = CurrencyManager.convertFromUsd(
                    product.minPrice.amount.toDoubleOrNull() ?: 0.0
                )
                val minPriceStr = if (convertedMinPrice % 1.0 == 0.0) {
                    "%.0f".format(convertedMinPrice)
                } else {
                    "%.2f".format(convertedMinPrice)
                }

                val convertedCompareAt = product.compareAtPrice?.amount?.toDoubleOrNull()
                    ?.let { CurrencyManager.convertFromUsd(it) }
                val compareAtStr = if (convertedCompareAt != null && convertedCompareAt > convertedMinPrice) {
                    if (convertedCompareAt % 1.0 == 0.0) {
                        "%.0f".format(convertedCompareAt)
                    } else {
                        "%.2f".format(convertedCompareAt)
                    }
                } else {
                    null
                }

                val totalReviews = product.reviews.size
                val averageRating = if (totalReviews > 0) product.reviews.map { it.rating }.average() else 0.0

                ProductInfoBlock(
                    title = state.translatedTitle ?: product.title,
                    currencyCode = currencyLabel,
                    amount = minPriceStr,
                    description = state.translatedDescription ?: product.description,
                    rating = averageRating,
                    reviewsCount = totalReviews,
                    compareAtAmount = compareAtStr,
                    discountPercent = product.discountPercent
                )
            }


            item {
                RatingSummaryBlock(reviews = product.reviews)
            }

            item {
                ReviewsListBlock(
                    reviews = product.reviews,
                    currentUser = state.currentUser,
                    onWriteReviewClick = onWriteReviewClick,
                    onEditReviewClick = onEditReviewClick,
                    onDeleteReviewClick = onDeleteReviewClick
                )
            }
        }

        ShopIQButton(
            text = stringResource(id = R.string.btn_add_to_cart),
            onClick = { onIntent(ProductDetailsIntent.AddToCart) },
            leadingIcon = Icons.Rounded.ShoppingBag,
            isLoading = state.isAddingToCart,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsContentPreview() {
    ShopIQTheme {
        ProductDetailsContent(
            state = previewUiState(),
            onIntent = {},
            onWriteReviewClick = {},
            onEditReviewClick = {},
            onDeleteReviewClick = {}
        )
    }
}

private fun previewUiState(): ProductDetailsUiState = ProductDetailsUiState(
    isLoading = false,
    error = null,
    product = previewProduct(),
    selectedImageIndex = 0,
    selectedColor = "Beige",
    isWishlisted = false,
    isAddingToCart = false,
    showUnauthorizedDialog = false
)

private fun previewProduct() = Product(
    id = "9746399428843",
    title = "Oversized Cotton Hoodie",
    description = "A relaxed-fit hoodie made from heavyweight cotton fleece, " +
            "featuring a kangaroo pocket and ribbed cuffs.",
    handle = "oversized-cotton-hoodie",
    productType = "Hoodie",
    vendor = "ShopIQ",
    tags = emptyList(),
    minPrice = Money(currencyCode = "EGP", amount = "1,299.00"),
    maxPrice = Money(currencyCode = "EGP", amount = "1,299.00"),
    images = listOf(
        ProductImage(
            url = "https://picsum.photos/seed/hoodie1/600/800",
            altText = "Oversized cotton hoodie, front view"
        ),
        ProductImage(
            url = "https://picsum.photos/seed/hoodie2/600/800",
            altText = "Oversized cotton hoodie, back view"
        )
    ),
    variants = emptyList(),
    isFavorite = false
)