package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iti.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllImagesSheet(
    images: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = stringResource(R.string.all_photos),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.heightIn(max = 480.dp)
        ) {
            itemsIndexed(images) { index, url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .border(
                            BorderStroke(
                                width = if (index == selectedIndex) 2.dp else 1.dp,
                                color = if (index == selectedIndex)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant
                            ),
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(index) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AllImagesSheetPreview() {
    var selectedIndex by remember { mutableStateOf(0) }

    MaterialTheme {
        AllImagesSheet(
            images = listOf(
                "https://picsum.photos/400?1",
                "https://picsum.photos/400?2",
                "https://picsum.photos/400?3",
                "https://picsum.photos/400?4",
                "https://picsum.photos/400?5",
                "https://picsum.photos/400?6"
            ),
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it },
            onDismiss = {}
        )
    }
}