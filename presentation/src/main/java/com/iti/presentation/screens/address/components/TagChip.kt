package com.iti.presentation.screens.address.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun TagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant

    val homeText = stringResource(R.string.address_tag_home)
    val workText = stringResource(R.string.address_tag_work)

    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            } else {
                val icon = when (label) {
                    homeText -> Icons.Default.Home
                    workText -> Icons.Default.Work
                    else -> Icons.Default.LocationOn
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

@Preview(name = "Light Mode - Selected")
@Composable
private fun TagChipSelectedLightPreview() {
    ShopIQTheme(darkTheme = false) {
        TagChip(
            label = "Home",
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Dark Mode - Selected")
@Composable
private fun TagChipSelectedDarkPreview() {
    ShopIQTheme(darkTheme = true) {
        TagChip(
            label = "Home",
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(name = "Light Mode - Unselected")
@Composable
private fun TagChipUnselectedLightPreview() {
    ShopIQTheme(darkTheme = false) {
        TagChip(
            label = "Work",
            isSelected = false,
            onClick = {}
        )
    }
}
