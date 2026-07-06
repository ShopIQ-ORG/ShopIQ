package com.iti.presentation.screens.orderdetails.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iti.presentation.util.shimmer

@Composable
fun OrderDetailsLoadingContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ShimmerInfoCard() }
        item { ShimmerBlock(modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp)) }
        items(3) { ShimmerItemRow() }
        item { ShimmerSummaryCard() }
        item { ShimmerAddressCard() }
    }
}

@Composable
private fun ShimmerInfoCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(13.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBlock(modifier = Modifier.width(120.dp).height(14.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerBlock(modifier = Modifier.width(90.dp).height(10.dp))
            }
            Spacer(Modifier.width(12.dp))
            ShimmerBlock(modifier = Modifier.width(64.dp).height(22.dp), shape = RoundedCornerShape(50))
        }
    }
}

@Composable
private fun ShimmerItemRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBlock(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBlock(modifier = Modifier.width(140.dp).height(14.dp))
            Spacer(Modifier.height(8.dp))
            ShimmerBlock(modifier = Modifier.width(80.dp).height(10.dp))
        }
        Spacer(Modifier.width(12.dp))
        ShimmerBlock(modifier = Modifier.width(48.dp).height(14.dp))
    }
}

@Composable
private fun ShimmerSummaryCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ShimmerBlock(modifier = Modifier.width(100.dp).height(14.dp))
            Spacer(Modifier.height(16.dp))
            repeat(3) {
                ShimmerBlock(modifier = Modifier.fillMaxWidth().height(12.dp))
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ShimmerAddressCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(11.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBlock(modifier = Modifier.width(100.dp).height(14.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerBlock(modifier = Modifier.fillMaxWidth().height(10.dp))
            }
        }
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