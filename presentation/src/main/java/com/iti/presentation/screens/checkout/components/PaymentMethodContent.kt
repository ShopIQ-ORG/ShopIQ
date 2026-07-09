package com.iti.presentation.screens.checkout.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQButton
import com.iti.presentation.screens.checkout.PaymentMethodType

@Composable
fun PaymentMethodContent(
    selectedMethod: PaymentMethodType?,
    onSelectMethod: (PaymentMethodType) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.payment_method_choose_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            PaymentMethodCard(
                title = stringResource(R.string.payment_method_cod_title),
                subtitle = stringResource(R.string.payment_method_cod_subtitle),
                icon = rememberVectorPainter(Icons.Outlined.Payments),
                type = PaymentMethodType.COD,
                isSelected = selectedMethod == PaymentMethodType.COD,
                onSelect = { onSelectMethod(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodCard(
                title = stringResource(R.string.payment_method_online_title),
                subtitle = stringResource(R.string.payment_method_online_subtitle),
                icon = rememberVectorPainter(Icons.Outlined.AccountBalanceWallet),
                type = PaymentMethodType.ONLINE,
                isSelected = selectedMethod == PaymentMethodType.ONLINE,
                onSelect = { onSelectMethod(it) }
            )
        }

        ShopIQButton(
            text = stringResource(R.string.payment_method_continue),
            onClick = onContinue,
            isLoading = isLoading,
            enabled = selectedMethod != null,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        )
    }
}
