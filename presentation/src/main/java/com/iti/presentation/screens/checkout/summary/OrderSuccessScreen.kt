package com.iti.presentation.screens.checkout.summary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.ui.theme.PrimaryLight
import com.iti.presentation.ui.theme.SearchFieldLight
import com.iti.presentation.ui.theme.SuccessContainerLight
import com.iti.presentation.ui.theme.SuccessLight
import com.iti.presentation.ui.theme.TextPrimaryLight
import com.iti.presentation.ui.theme.TextSecondaryLight

@Composable
fun OrderSuccessScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    // Block system back — user must use one of the two buttons
    BackHandler { onNavigateToHome() }

    // Animate the check icon
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Success icon ──
        Surface(
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value),
            shape = CircleShape,
            color = SuccessContainerLight
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Payment Successful",
                modifier = Modifier.padding(24.dp),
                tint = SuccessLight
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Title ──
        Text(
            text = "Payment Successful!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Subtitle ──
        Text(
            text = "Your order has been placed successfully.\nYou'll receive a confirmation shortly.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryLight,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ── Primary CTA: View Orders ──
        Button(
            onClick = onNavigateToOrders,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C222B))
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "View My Orders",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── Secondary CTA: Continue Shopping ──
        OutlinedButton(
            onClick = onNavigateToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryLight),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextPrimaryLight
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Continue Shopping",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Footer ──
        Text(
            text = "Thank you for shopping with ShopIQ ❤\uFE0F",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryLight.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
