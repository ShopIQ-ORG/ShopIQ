package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    if(reviews.isEmpty()) return
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
