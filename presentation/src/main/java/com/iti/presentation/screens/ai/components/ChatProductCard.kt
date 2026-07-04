package com.iti.presentation.screens.ai.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.components.CustomNetworkImage
import com.iti.presentation.screens.ai.AiChatContract.ChatProductUi

@Composable
fun SingleProductCard(
    product: ChatProductUi,
    isDark: Boolean,
    onProductClick: (Long) -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1E242B) else Color.White
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomNetworkImage(
                    imageUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = product.details,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.price,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "  •  ",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                        Text(
                            text = product.stockStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (product.stockStatus.contains("In Stock")) Color(0xFF2E7D32) else Color(0xFFD84315)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = {
                    val idLong = product.id.substringAfterLast("/").toLongOrNull() ?: 0L
                    onProductClick(idLong)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = ButtonDefaults.outlinedButtonBorder(
                    enabled = true
                ).copy(
                    brush = Brush.linearGradient(listOf(borderColor, borderColor))
                )
            ) {
                Text(
                    text = stringResource(id = R.string.ai_product_view),
                    color = if (isDark) Color.White else Color(0xFF6F32E5),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ProductSuggestionsCard(
    products: List<ChatProductUi>,
    isDark: Boolean,
    onProductClick: (Long) -> Unit
) {
    val cardBg = if (isDark) Color(0xFF1E242B) else Color.White
    val borderColor = if (isDark) Color(0xFF2E3844) else Color(0xFFE5E7EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column {
            products.forEachIndexed { index, product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val idLong = product.id.substringAfterLast("/").toLongOrNull() ?: 0L
                            onProductClick(idLong)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomNetworkImage(
                        imageUrl = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.price,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "  •  ",
                                fontSize = 12.sp,
                                color = Color.LightGray
                            )
                            Text(
                                text = product.stockStatus,
                                fontSize = 11.sp,
                                color = if (product.stockStatus.contains("In Stock")) Color(0xFF2E7D32) else Color(0xFFD84315)
                            )
                        }
                    }
                }
                
                if (index < products.lastIndex) {
                    HorizontalDivider(color = borderColor)
                }
            }
        }
    }
}
