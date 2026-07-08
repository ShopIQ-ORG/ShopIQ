package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.ProductReview
import com.iti.presentation.R
import java.util.Locale

@Composable
fun RatingSummaryBlock(
    reviews: List<ProductReview>,
    modifier: Modifier = Modifier
) {
    val finalReviews = getReviewsOrDefault(reviews)
    val totalCount = finalReviews.size
    val averageRating = if (totalCount > 0) {
        finalReviews.map { it.rating }.average()
    } else 0.0

    // Rating counts breakdown
    val rating5 = finalReviews.count { it.rating == 5 }
    val rating4 = finalReviews.count { it.rating == 4 }
    val rating3 = finalReviews.count { it.rating == 3 }
    val rating2 = finalReviews.count { it.rating == 2 }
    val rating1 = finalReviews.count { it.rating == 1 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Rating score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.1f)
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f", averageRating),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = stringResource(id = R.string.rating_out_of_5),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                DecimalStarRatingRow(
                    rating = averageRating,
                    starSize = 20.dp
                )
                Text(
                    text = "($totalCount ${stringResource(id = R.string.ratings_count)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Vertical Divider
            Box(
                modifier = Modifier
                    .height(90.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp)
            )

            // Right side: Progress bars breakdown
            Column(
                modifier = Modifier
                    .weight(1.9f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RatingDistributionRow(stars = 5, count = rating5, total = totalCount)
                RatingDistributionRow(stars = 4, count = rating4, total = totalCount)
                RatingDistributionRow(stars = 3, count = rating3, total = totalCount)
                RatingDistributionRow(stars = 2, count = rating2, total = totalCount)
                RatingDistributionRow(stars = 1, count = rating1, total = totalCount)
            }
        }
    }
}

@Composable
fun RatingDistributionRow(
    stars: Int,
    count: Int,
    total: Int
) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(targetValue = fraction, label = "progress")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stars.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(12.dp),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.width(8.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape),
            color = Color(0xFFFFA726), // Premium gold/orange
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = count.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ReviewsListBlock(
    reviews: List<ProductReview>,
    currentUserName: String?,
    onWriteReviewClick: () -> Unit,
    onEditReviewClick: (ProductReview) -> Unit,
    onDeleteReviewClick: (ProductReview) -> Unit,
    modifier: Modifier = Modifier
) {
    val finalReviews = getReviewsOrDefault(reviews)
    val totalCount = finalReviews.size

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(id = R.string.all_reviews)} ($totalCount)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sort Dropdown
                Row(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .clickable { /* Handle sort */ }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.sort_latest),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Write Review button
        OutlinedButton(
            onClick = onWriteReviewClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.write_review_btn),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reviews list or empty state
        if (totalCount == 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Text(
                    text = stringResource(id = R.string.no_reviews_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                finalReviews.forEach { review ->
                    val isOwnReview = currentUserName != null && review.customerName == currentUserName
                    ReviewItem(
                        review = review,
                        isOwnReview = isOwnReview,
                        onEditClick = { onEditReviewClick(review) },
                        onDeleteClick = { onDeleteReviewClick(review) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: ProductReview,
    isOwnReview: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialHelpfulCount = remember(review.id) { if (review.id.startsWith("mock")) (1..15).random() else 0 }
    var helpfulCount by remember { mutableStateOf(initialHelpfulCount) }
    var isHelpfulClicked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Avatar + Name + Date + Verified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isImageError by remember { mutableStateOf(false) }
                val showInitials = review.avatarUrl.isNullOrBlank() || isImageError

                if (!showInitials) {
                    coil.compose.AsyncImage(
                        model = review.avatarUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        onError = { isImageError = true }
                    )
                } else {
                    val initials = if (review.customerName.isNotBlank()) {
                        review.customerName.trim().first().uppercase()
                    } else "A"
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.customerName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatReviewDate(review.createdAt),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }


            }

            Spacer(modifier = Modifier.height(10.dp))

            // Star Rating
            StarRatingRow(rating = review.rating, starSize = 16.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Review Body Text
            Text(
                text = review.body,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Start
            )

            // Bottom Actions Row: Vertical dots + Helpful thumbs up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vertical dots menu
                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { if (isOwnReview) showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isOwnReview) 0.6f else 0.2f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (isOwnReview) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.edit_review_label), fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.delete_review_label), color = MaterialTheme.colorScheme.error, fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }

                // Helpful Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!isHelpfulClicked) {
                            helpfulCount++
                            isHelpfulClicked = true
                        } else {
                            helpfulCount--
                            isHelpfulClicked = false
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.was_review_helpful),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Helpful",
                        tint = if (isHelpfulClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = helpfulCount.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHelpfulClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StarRatingRow(
    rating: Int,
    maxStars: Int = 5,
    starSize: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFFFA726),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
fun DecimalStarRatingRow(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 18.dp
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starValue = index + 1
            val isFull = rating >= starValue
            val isHalf = !isFull && rating >= (starValue - 0.5)

            Icon(
                imageVector = when {
                    isFull -> Icons.Filled.Star
                    isHalf -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Outlined.Star
                },
                contentDescription = null,
                tint = Color(0xFFFFA726),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

fun getReviewsOrDefault(reviews: List<ProductReview>): List<ProductReview> {
    return reviews
}

fun formatReviewDate(rawDate: String): String {
    return try {
        // Try parsing ISO-8601 UTC timestamp
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(rawDate) ?: return rawDate

        val outputFormat = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        rawDate
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, title: String, body: String) -> Unit,
    isSubmitting: Boolean,
    error: String?,
    initialRating: Int = 5,
    initialTitle: String = "",
    initialBody: String = ""
) {
    var rating by remember { mutableStateOf(initialRating) }
    var title by remember { mutableStateOf(initialTitle) }
    var body by remember { mutableStateOf(initialBody) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var bodyError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.add_review_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Star Rating Selector
                Column {
                    Text(
                        text = stringResource(id = R.string.rating_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val starValue = index + 1
                            IconButton(
                                onClick = { rating = starValue },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (starValue <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA726),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Title Input
                Column {
                    Text(
                        text = stringResource(id = R.string.title_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = if (it.isBlank()) "Title is required" else null
                        },
                        placeholder = { Text(stringResource(id = R.string.review_title_placeholder)) },
                        isError = titleError != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (titleError != null) {
                        Text(
                            text = titleError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }

                // Body Input
                Column {
                    Text(
                        text = stringResource(id = R.string.body_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = body,
                        onValueChange = {
                            body = it
                            bodyError = if (it.isBlank()) "Body is required" else null
                        },
                        placeholder = { Text(stringResource(id = R.string.review_body_placeholder)) },
                        isError = bodyError != null,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )
                    if (bodyError != null) {
                        Text(
                            text = bodyError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = "Title is required"
                            }
                            if (body.isBlank()) {
                                bodyError = "Body is required"
                            }
                            if (title.isNotBlank() && body.isNotBlank()) {
                                onSubmit(rating, title, body)
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.submit_btn),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
