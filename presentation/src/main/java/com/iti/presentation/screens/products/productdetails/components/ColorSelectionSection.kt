package com.iti.presentation.screens.products.productdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.ui.theme.ShopIQTheme

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


@Preview( showBackground = true)
@Composable
private fun ColorSelectionSectionBeigePreview() {
    ShopIQTheme {
        ColorSelectionSection(
            selectedColor = "Beige",
            onColorSelect = {}
        )
    }
}


