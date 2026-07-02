//
//  AddressLocationDetected.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.address.components

import com.iti.presentation.ui.theme.LocalDarkTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Address
import com.iti.presentation.components.ShopIQButton

@Composable
fun AddressLocationDetected(
    address: Address,
    onConfirmClick: (name: String, isDefault: Boolean) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTag by remember { mutableStateOf("Home") }
    var customTag by remember { mutableStateOf("") }
    var isDefaultAddress by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Map Preview Snapshot Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            MapPreviewPlaceholder()
        }

        // Address Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detected Address",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF4CAF50)
                    )
                }

                Text(
                    text = address.street,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (address.city.isNotEmpty() || address.postalCode.isNotEmpty()) {
                    Text(
                        text = "${address.city} ${address.postalCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = address.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tags & Details Form
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Save Address As",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TagChip(
                    label = "Home",
                    isSelected = selectedTag == "Home",
                    onClick = { selectedTag = "Home" }
                )
                TagChip(
                    label = "Work",
                    isSelected = selectedTag == "Work",
                    onClick = { selectedTag = "Work" }
                )
                TagChip(
                    label = "Other",
                    isSelected = selectedTag == "Other",
                    onClick = { selectedTag = "Other" }
                )
            }

            if (selectedTag == "Other") {
                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    label = { Text("Custom Tag Name (e.g. Gym, Friend's House)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Set default switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Set as default address",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Use this address for default delivery",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isDefaultAddress,
                    onCheckedChange = { isDefaultAddress = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmation Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShopIQButton(
                text = "Confirm Address",
                onClick = {
                    val finalTagName = if (selectedTag == "Other") {
                        customTag.ifBlank { "Other" }
                    } else {
                        selectedTag
                    }
                    onConfirmClick(finalTagName, isDefaultAddress)
                },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = onCancelClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Use This Location Instead",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = Modifier
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
                    "Home" -> Icons.Default.Home
                    "Work" -> Icons.Default.Work
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

@Composable
fun MapPreviewPlaceholder() {
    val isDark = LocalDarkTheme.current
    val gridColor = if (isDark) Color(0xFF232A34) else Color(0xFFE5E9EE)
    val mapBackground = if (isDark) Color(0xFF1A1F26) else Color(0xFFF0F3F6)
    val streetColor = if (isDark) Color(0xFF2C3542) else Color(0xFFFFFFFF)
    
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // Draw Map Background
        drawRect(color = mapBackground)

        // Draw street grid (horizontal, vertical, diagonal)
        val numLines = 8
        val spacingW = size.width / numLines
        val spacingH = size.height / numLines

        // Major street lines (white/light grey roads)
        // Horizontal main road
        drawRect(
            color = streetColor,
            topLeft = Offset(0f, size.height * 0.4f),
            size = androidx.compose.ui.geometry.Size(size.width, 18.dp.toPx())
        )
        // Vertical main road
        drawRect(
            color = streetColor,
            topLeft = Offset(size.width * 0.5f - 9.dp.toPx(), 0f),
            size = androidx.compose.ui.geometry.Size(18.dp.toPx(), size.height)
        )
        // Diagonal main road
        drawLine(
            color = streetColor,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 14.dp.toPx()
        )

        // Grid overlay lines (subtle lines)
        for (i in 1..numLines) {
            drawLine(
                color = gridColor,
                start = Offset(i * spacingW, 0f),
                end = Offset(i * spacingW, size.height),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = gridColor,
                start = Offset(0f, i * spacingH),
                end = Offset(size.width, i * spacingH),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw GPS location center point (Blue dot)
        val center = Offset(size.width * 0.5f, size.height * 0.5f)
        
        // Draw accuracy circle
        drawCircle(
            color = Color(0xFF2196F3).copy(alpha = 0.15f),
            radius = 60.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color(0xFF2196F3).copy(alpha = 0.3f),
            radius = 30.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = center
        )
        drawCircle(
            color = Color(0xFF2196F3),
            radius = 4.dp.toPx(),
            center = center
        )

        // Draw Marker Pin (Green Pin above the center)
        val pinCenter = Offset(size.width * 0.5f, size.height * 0.5f - 24.dp.toPx())
        val pinPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(pinCenter.x, pinCenter.y + 12.dp.toPx())
            cubicTo(
                pinCenter.x - 10.dp.toPx(), pinCenter.y - 4.dp.toPx(),
                pinCenter.x - 10.dp.toPx(), pinCenter.y - 16.dp.toPx(),
                pinCenter.x, pinCenter.y - 16.dp.toPx()
            )
            cubicTo(
                pinCenter.x + 10.dp.toPx(), pinCenter.y - 16.dp.toPx(),
                pinCenter.x + 10.dp.toPx(), pinCenter.y - 4.dp.toPx(),
                pinCenter.x, pinCenter.y + 12.dp.toPx()
            )
            close()
        }
        
        drawPath(
            path = pinPath,
            color = Color(0xFF4CAF50)
        )
        // Marker inner dot
        drawCircle(
            color = Color.White,
            radius = 3.dp.toPx(),
            center = Offset(pinCenter.x, pinCenter.y - 5.dp.toPx())
        )
    }
}
