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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.ui.theme.*

@Composable
fun CheckoutStepper(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val doneColor = if (isDark) SuccessDark else SuccessLight
    val incomingColor = if (isDark) WarningDark else WarningLight
    val currentColor = if (isDark) Color.White else Color.Black
    val onCurrentColor = if (isDark) Color.Black else Color.White
    val onIncomingColor = Color.Black
    val onDoneColor = Color.White

    val steps = listOf(
        stringResource(R.string.checkout_step_address),
        stringResource(R.string.checkout_step_payment),
        stringResource(R.string.checkout_step_summary),
        stringResource(R.string.checkout_step_success)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            steps.forEachIndexed { index, _ ->
                val stepNumber = index + 1
                val isDone = stepNumber < currentStep
                val isActive = stepNumber == currentStep

                val containerColor = when {
                    isDone -> doneColor
                    isActive -> currentColor
                    else -> incomingColor
                }

                val contentColor = when {
                    isDone -> onDoneColor
                    isActive -> onCurrentColor
                    else -> onIncomingColor
                }

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

                if (index < steps.lastIndex) {
                    val lineDone = stepNumber < currentStep
                    val lineColor = if (lineDone) doneColor else MaterialTheme.colorScheme.outlineVariant
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        thickness = 2.dp,
                        color = lineColor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (currentStep) {
                1 -> stringResource(R.string.checkout_step_address)
                2 -> stringResource(R.string.checkout_step_payment)
                3 -> stringResource(R.string.checkout_step_summary)
                else -> stringResource(R.string.checkout_step_success)
            },
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
