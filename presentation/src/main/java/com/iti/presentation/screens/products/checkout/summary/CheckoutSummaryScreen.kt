package com.iti.presentation.screens.products.checkout.summary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.presentation.ui.theme.PrimaryLight
import com.iti.presentation.ui.theme.SearchFieldLight
import com.iti.presentation.ui.theme.SuccessContainerLight
import com.iti.presentation.ui.theme.TextPrimaryLight
import com.iti.presentation.ui.theme.TextSecondaryLight
import com.iti.presentation.ui.theme.SuccessLight
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.screens.products.checkout.PaymentMethodContract.PaymentMethodType
import com.iti.presentation.screens.products.checkout.summary.components.PaymobBottomSheet
import com.iti.presentation.screens.cart.components.OrderSummary
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import com.iti.presentation.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutSummaryScreen(
    paymentMethod: PaymentMethodType,
    onNavigateBack: () -> Unit,
    onNavigateToOrderConfirmation: () -> Unit,
    viewModel: CheckoutSummaryViewModel = koinViewModel(parameters = { parametersOf(paymentMethod) })
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CheckoutSummaryContract.Effect.NavigateBack -> onNavigateBack()
                CheckoutSummaryContract.Effect.NavigateToOrderConfirmation -> onNavigateToOrderConfirmation()
                is CheckoutSummaryContract.Effect.ShowError -> {
                    // Show snackbar or toast
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BackTopBar(
                title = if (state.paymentMethod == PaymentMethodType.COD) "Cash on Delivery" else "Online Payment",
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = { viewModel.onEvent(CheckoutSummaryContract.Event.PlaceOrderClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1C222B),
                        contentColor = Color.White
                    ),
                    enabled = !state.isPlacingOrder && !state.paymentProcessing,
                    elevation = null
                ) {
                    if (state.isPlacingOrder || state.paymentProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (state.paymentMethod == PaymentMethodType.COD) "Place Order" else "Pay Securely",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryLight)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (state.paymentMethod == PaymentMethodType.COD) {
                    item {
                        CODHeaderSection()
                    }
                }

                item {
                    PaddingWrapper {
                        AddressSummarySection(address = state.address)
                    }
                }

                if (state.paymentMethod == PaymentMethodType.COD) {
                    item {
                        PaddingWrapper {
                            Spacer(Modifier.height(20.dp))
                            CODInfoSection()
                        }
                    }
                    item {
                        PaddingWrapper {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = SearchFieldLight)
                        }
                    }
                    item {
                        PaddingWrapper {
                            state.cart?.let { CODSummarySection(cart = it) }
                        }
                    }
                    item {
                        Spacer(Modifier.height(32.dp))
                        PaddingWrapper {
                            CODNoteSection()
                        }
                    }
                } else {
                    item {
                        PaddingWrapper {
                            Spacer(Modifier.height(20.dp))
                            ShippingMethodSection()
                        }
                    }

                    item {
                        PaddingWrapper {
                            Spacer(Modifier.height(20.dp))
                            if (state.isLoading) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = SearchFieldLight.copy(alpha = 0.5f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = PrimaryLight,
                                                strokeWidth = 3.dp
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "Calculating fees...",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            } else {
                                state.cart?.let {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Receipt,
                                                contentDescription = null,
                                                tint = TextPrimaryLight,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Order Summary",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = SearchFieldLight.copy(alpha = 0.5f))
                                        ) {
                                            OrderSummary(cart = it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (state.showPaymobBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onEvent(CheckoutSummaryContract.Event.DismissPaymobBottomSheet) },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                PaymobBottomSheet(
                    amount = state.cart?.total?.amount ?: "0.0",
                    onPaymentSuccess = { response ->
                        viewModel.onEvent(CheckoutSummaryContract.Event.OnPaymentSuccess(response))
                    },
                    onPaymentFailure = { error ->
                        viewModel.onEvent(CheckoutSummaryContract.Event.OnPaymentFailure(error))
                    },
                    onClose = { viewModel.onEvent(CheckoutSummaryContract.Event.DismissPaymobBottomSheet) }
                )
            }
        }
    }
}

@Composable
fun PaddingWrapper(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        content()
    }
}

@Composable
fun CODHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Professional illustration placeholder
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = SearchFieldLight
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp),
                    tint = PrimaryLight.copy(alpha = 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cash on Delivery",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryLight
            )
        }
    }
}

@Composable
fun CODInfoSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE3F2FD).copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "COD Policy",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append("You can place orders up to ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimaryLight)) {
                            append("$500.00")
                        }
                        append(" using Cash on Delivery.")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun CODSummarySection(cart: Cart) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SummaryRow(label = "Order Total", value = "$${cart.total.amount}")
        SummaryRow(label = "COD Fee", value = "$0.00")
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Payable on Delivery",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryLight
            )
            Text(
                text = "$${cart.total.amount}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryLight
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryLight
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = TextPrimaryLight
        )
    }
}

@Composable
fun CODNoteSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SuccessContainerLight
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                tint = SuccessLight,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Please keep exact change for a smooth delivery.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = SuccessLight
            )
        }
    }
}

@Composable
fun AddressSummarySection(address: Address?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Delivery Address",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address?.name ?: "No address selected",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = address?.city ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { /* Navigate to address selection */ }) {
                    Text("Change")
                }
            }
        }
    }
}

@Composable
fun ShippingMethodSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = TextPrimaryLight,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Shipping Method",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = true, onClick = null)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Standard Delivery (3-5 days)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Text(
                    text = "Free",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
