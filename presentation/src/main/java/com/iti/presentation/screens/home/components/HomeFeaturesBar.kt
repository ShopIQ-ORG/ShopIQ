package com.iti.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.iti.presentation.R

@Composable
fun HomeFeaturesBar(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Free Delivery Feature
            FeatureItem(
                icon = Icons.Default.LocalShipping,
                title = stringResource(R.string.free_delivery_title),
                subtitle = stringResource(R.string.free_delivery_desc),
                modifier = Modifier.weight(1f)
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color(0xFFE5E7EB))
            )

            // Easy Returns Feature
            FeatureItem(
                icon = Icons.AutoMirrored.Filled.AssignmentReturn,
                title = stringResource(R.string.easy_returns_title),
                subtitle = stringResource(R.string.easy_returns_desc),
                modifier = Modifier.weight(1f)
            )

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color(0xFFE5E7EB))
            )

            // Secure Payment Feature
            FeatureItem(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.secure_payment_title),
                subtitle = stringResource(R.string.secure_payment_desc),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.sp,
                    lineHeight = 10.sp
                ),
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFeaturesBarPreview() {
    HomeFeaturesBar()
}
