//
//  CheckoutStepper.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iti.presentation.R

@Composable
fun CheckoutStepper(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    // Dynamically calculate the horizontal offset so connecting lines align exactly with circle centers.
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val halfStepWidth = screenWidth / 10

    val steps = listOf(
        stringResource(R.string.checkout_step_address),
        stringResource(R.string.checkout_step_method),
        stringResource(R.string.checkout_step_payment),
        stringResource(R.string.checkout_step_summary),
        stringResource(R.string.checkout_step_success)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Background connecting lines between circle centers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = halfStepWidth)
                    .height(36.dp), // Align vertically with the center of the 36.dp circles
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = if (currentStep > 1) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = if (currentStep > 2) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = if (currentStep > 3) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 2.dp,
                    color = if (currentStep > 4) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outlineVariant
                )
            }

            // Foreground step items (circle + description text)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                steps.forEachIndexed { index, title ->
                    val stepNumber = index + 1
                    val isDone = stepNumber < currentStep
                    val isActive = stepNumber == currentStep
                    val isActiveOrDone = stepNumber <= currentStep

                    val containerColor = when {
                        isDone -> MaterialTheme.colorScheme.tertiary
                        isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }

                    val contentColor = when {
                        isDone -> MaterialTheme.colorScheme.onTertiary
                        isActive -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(containerColor)
                                .border(
                                    width = 1.dp,
                                    color = if (isActive) MaterialTheme.colorScheme.outline else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = contentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = stepNumber.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = contentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isActiveOrDone) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isActiveOrDone) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            },
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
