//
//  SuccessStepContent.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.checkout.DraftOrder
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.ui.theme.LocalDarkTheme
import com.iti.presentation.ui.theme.SuccessDark
import com.iti.presentation.ui.theme.SuccessLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessStepContent(
    draftOrder: DraftOrder?,
    currentUser: com.iti.domain.models.User?,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isDark = LocalDarkTheme.current

    // Extract first name or fallback
    val customerName = when (currentUser) {
        is com.iti.domain.models.User.AuthenticatedUser -> {
            currentUser.fullName.split(" ").firstOrNull() ?: "Customer"
        }
        else -> "Customer"
    }

    val customerEmail = when (currentUser) {
        is com.iti.domain.models.User.AuthenticatedUser -> currentUser.email
        else -> "your registered email"
    }

    // Format current date matching the design (e.g. May 12, 2024)
    val dateStr = try {
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
        dateFormat.format(java.util.Date())
    } catch (e: Exception) {
        "Today"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            BackTopBar(
                title = "Order Confirmed",
                onBack = onGoHome
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color.White else Color(0xFF1E293B),
                        contentColor = if (isDark) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Continue Shopping",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Premium Confetti + Checkmark Circle
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Success Circle
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(if (isDark) SuccessDark else SuccessLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Top-left blue dash
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 28.dp, y = 16.dp)
                        .size(width = 4.dp, height = 12.dp)
                        .rotate(-30f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF3B82F6))
                )

                // Top-right orange dash
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-28).dp, y = 18.dp)
                        .size(width = 4.dp, height = 12.dp)
                        .rotate(35f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFF59E0B))
                )

                // Middle-left orange square
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 18.dp, y = (-24).dp)
                        .size(width = 10.dp, height = 6.dp)
                        .rotate(15f)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFFF97316))
                )

                // Middle-right blue dot
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-20).dp, y = (-12).dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF60A5FA))
                )

                // Bottom-left orange square
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 24.dp, y = (-20).dp)
                        .size(width = 8.dp, height = 8.dp)
                        .rotate(45f)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color(0xFFF97316))
                )

                // Bottom-right red diamond
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-24).dp, y = (-24).dp)
                        .size(6.dp)
                        .rotate(45f)
                        .background(Color(0xFFEF4444))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Greeting title
            Text(
                text = "Thank you, $customerName! 🎉",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Placing subtitle
            Text(
                text = "Your order has been placed successfully.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Order Number",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val dispNum = draftOrder?.orderNumber ?: draftOrder?.id?.substringAfterLast("/") ?: "ORD123456"
                        val prefix = if (dispNum.startsWith("#")) "" else "#"
                        Text(
                            text = prefix + dispNum,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Paid",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${draftOrder?.totalPrice ?: "0.00"} EGP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation Email Note
            Text(
                text = "A confirmation email has been sent to",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = customerEmail,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
