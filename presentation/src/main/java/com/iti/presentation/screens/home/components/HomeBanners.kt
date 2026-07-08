package com.iti.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.iti.presentation.R

@Composable
fun HomeBanners(
    onExploreClick: () -> Unit,
    onShopNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = com.iti.presentation.ui.theme.LocalDarkTheme.current

    val newArrivalsBg = if (isDark) Color(0xFF1B3B2B) else Color(0xFFE8F5E9)
    val newArrivalsTitle = if (isDark) Color(0xFF81C784) else Color(0xFF0F5132)
    val newArrivalsDesc = if (isDark) Color(0xFFA5D6A7) else Color(0xFF2E6540)
    val newArrivalsBtnBg = if (isDark) Color(0xFF2E6540) else Color(0xFFA3E2B6)
    val newArrivalsBtnText = if (isDark) Color.White else Color(0xFF0F5132)

    val summerSaleBg = if (isDark) Color(0xFF3E2723) else Color(0xFFFFF9E6)
    val summerSaleTitle = if (isDark) Color(0xFFFFB74D) else Color(0xFF6B4B02)
    val summerSaleDesc = if (isDark) Color(0xFFFFCC80) else Color(0xFF9E7E38)
    val summerSaleBtnBg = if (isDark) Color(0xFF9E7E38) else Color(0xFFFFD54F)
    val summerSaleBtnText = if (isDark) Color.White else Color(0xFF5D4037)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // New Arrivals Banner (Full width)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable { onExploreClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = newArrivalsBg
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background image positioned on the right loaded asynchronously
                AsyncImage(
                    model = R.drawable.new_arrival,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxHeight()
                        .width(150.dp),
                    contentScale = ContentScale.Fit
                )

                // Text Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.new_arrivals_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = newArrivalsTitle
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.new_arrivals_desc).replace("\n", " "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = newArrivalsDesc
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(newArrivalsBtnBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.explore_btn),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = newArrivalsBtnText
                        )
                    }
                }
            }
        }

        // Summer Sale Banner (Full width)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable { onShopNowClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = summerSaleBg
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background image positioned on the right loaded asynchronously
                AsyncImage(
                    model = R.drawable.summer_sale,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxHeight()
                        .width(160.dp),
                    contentScale = ContentScale.Fit
                )

                // Text Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.summer_sale_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = summerSaleTitle
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.summer_sale_desc).replace("\n", " "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = summerSaleDesc
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(summerSaleBtnBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.shop_now_btn),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = summerSaleBtnText
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBannersPreview() {
    HomeBanners(
        onExploreClick = {},
        onShopNowClick = {}
    )
}
