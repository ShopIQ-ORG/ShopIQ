package com.iti.presentation.screens.orders.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.shimmer

@Composable
fun OrdersLoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(6) {
            OrderCardShimmer()
        }
    }
}

@Composable
fun OrderCardShimmer() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShimmerHeader()
            Spacer(Modifier.height(18.dp))
            ShimmerMetricsRow()
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))
            ShimmerFooter()
        }
    }
}

@Composable
private fun ShimmerHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ShimmerBlock(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(13.dp))

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            ShimmerBlock(modifier = Modifier.width(110.dp).height(14.dp))
            Spacer(Modifier.height(6.dp))
            ShimmerBlock(modifier = Modifier.width(80.dp).height(10.dp))
        }

        Spacer(Modifier.width(12.dp))

        ShimmerBlock(modifier = Modifier.width(64.dp).height(22.dp), shape = RoundedCornerShape(50))
    }
}

@Composable
private fun ShimmerMetricsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerMetricItem()

        VerticalDivider(
            modifier = Modifier.height(32.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        ShimmerMetricItem()
    }
}

@Composable
private fun ShimmerMetricItem() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ShimmerBlock(modifier = Modifier.size(16.dp), shape = RoundedCornerShape(4.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            ShimmerBlock(modifier = Modifier.width(56.dp).height(13.dp))
            Spacer(Modifier.height(6.dp))
            ShimmerBlock(modifier = Modifier.width(40.dp).height(10.dp))
        }
    }
}

@Composable
private fun ShimmerFooter() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(14.dp))
        ShimmerBlock(modifier = Modifier.size(16.dp), shape = RoundedCornerShape(4.dp))
    }
}

@Composable
private fun ShimmerBlock(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .shimmer()
    )
}

@Preview(showBackground = true)
@Composable
private fun OrderCardShimmerPreview() {
    ShopIQTheme {
        OrderCardShimmer()
    }
}

@Preview(showBackground = true)
@Composable
private fun OrdersLoadingContentPreview() {
    ShopIQTheme {
        OrdersLoadingContent()
    }
}