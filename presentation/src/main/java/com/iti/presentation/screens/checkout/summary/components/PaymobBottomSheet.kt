package com.iti.presentation.screens.checkout.summary.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.presentation.R

import androidx.compose.ui.text.style.TextAlign
import com.iti.presentation.ui.theme.PrimaryLight
import com.iti.presentation.ui.theme.SearchFieldLight
import com.iti.presentation.ui.theme.TextPrimaryLight
import com.iti.presentation.ui.theme.TextSecondaryLight

@Composable
fun PaymobBottomSheet(
    amount: String,
    onPaymentSuccess: (Map<String, String?>) -> Unit,
    onPaymentFailure: (String) -> Unit,
    onClose: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var nameOnCard by remember { mutableStateOf("") }
    var saveCard by remember { mutableStateOf(true) }

    val isFormValid = cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty() && nameOnCard.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp, top = 8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Secure Checkout",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryLight
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimaryLight)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Card Information",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextSecondaryLight
            )
            
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16) cardNumber = it },
                label = { Text("Card Number") },
                placeholder = { Text("0000 0000 0000 0000") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = PrimaryLight)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryLight,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { if (it.length <= 5) expiryDate = it },
                    label = { Text("Expiry (MM/YY)") },
                    placeholder = { Text("MM/YY") },
                    modifier = Modifier.weight(1.5f), // Larger weight for Expiry
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryLight,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text("CVV") },
                    placeholder = { Text("123") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryLight) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryLight,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            OutlinedTextField(
                value = nameOnCard,
                onValueChange = { nameOnCard = it },
                label = { Text("Cardholder Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryLight) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryLight,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = saveCard,
                onCheckedChange = { saveCard = it },
                colors = CheckboxDefaults.colors(checkedColor = PrimaryLight)
            )
            Text(
                text = "Save card for future use",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight
            )
        }

        Button(
            onClick = { 
                if (isFormValid) {
                    onPaymentSuccess(mapOf("status" to "success"))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) Color(0xFF1C222B) else SearchFieldLight,
                contentColor = if (isFormValid) Color.White else Color.Gray
            ),
            enabled = isFormValid,
            elevation = null
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Pay Securely ($amount)",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Secured and powered by ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
                Text(
                    text = "paymob",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFF0055FF)
                )
            }
            Text(
                text = "Your payment information is encrypted and secure.",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryLight.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
