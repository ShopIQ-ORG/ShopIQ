package com.iti.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result

@Composable
fun HomeTabContent(
    productsResult: Result<List<Product>>,
    brandsResult: Result<List<Brand>>,
    adsResult: Result<List<Ad>>,
    onNavigateToAllBrands: () -> Unit = {},
    onNavigateToAllProducts: (String?) -> Unit = {}
) {
    // Single LazyColumn for the entire screen ensures everything scrolls together
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB)), // Light, professional background
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Search Bar
        item {
            SearchBar()
        }

        // Ads Section
        item {
            AdsSection(adsResult)
        }

        // Brands Section
        item {
            SectionHeader(title = "Top Brands", onViewAllClick = onNavigateToAllBrands)
            BrandsSection(brandsResult, onBrandClick = onNavigateToAllProducts)
        }

        // Products Header
        item {
            SectionHeader(title = "Featured Products", onViewAllClick = { onNavigateToAllProducts(null) })
        }

        // Products Grid (Show top 6 products in a clean 2-column layout)
        when (productsResult) {
            is Result.Success -> {
                val products = productsResult.data.take(6) // Only show top 6
                val rowCount = (products.size + 1) / 2
                items(rowCount) { rowIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val firstProductIndex = rowIndex * 2
                        ProductItem(
                            product = products[firstProductIndex],
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (firstProductIndex + 1 < products.size) {
                            ProductItem(
                                product = products[firstProductIndex + 1],
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is Result.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }
            is Result.Failure -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Error loading products", color = Color.Red)
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(50.dp),
        placeholder = { Text("Search for products, brands...", fontSize = 13.sp, color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray) },
        shape = CircleShape,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF3F4F6),
            unfocusedContainerColor = Color(0xFFF3F4F6),
            disabledContainerColor = Color(0xFFF3F4F6),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        )
    )
}

@Composable
fun AdsSection(adsResult: Result<List<Ad>>) {
    // High-quality fallback ads matching the Spring/Summer fashion theme of the mockup
    val defaultAds = remember {
        listOf(
            Ad(
                id = "1",
                imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=600&q=80",
                title = "NEW COLLECTION",
                subtitle = "SPRING / SUMMER '24"
            ),
            Ad(
                id = "2",
                imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600&q=80",
                title = "EXCLUSIVE OFFER",
                subtitle = "CLASSY OUT FITS"
            ),
            Ad(
                id = "3",
                imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600&q=80",
                title = "STREET WEAR",
                subtitle = "URBAN VIBES '24"
            )
        )
    }

    val ads = when (adsResult) {
        is Result.Success -> {
            val validAds = adsResult.data.filter { it.imageUrl.isNotEmpty() }
            if (validAds.isEmpty()) defaultAds else validAds
        }
        else -> defaultAds
    }

    val pagerState = rememberPagerState(pageCount = { ads.size })

    // Auto-scroll effect (3 seconds interval)
    LaunchedEffect(pagerState) {
        while (true) {
            kotlinx.coroutines.delay(4000)
            if (ads.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % ads.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val ad = ads[page]
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Image as background
                SubcomposeAsyncImage(
                    model = ad.imageUrl,
                    contentDescription = ad.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.Black)
                        }
                    }
                )
                
                // Semi-transparent overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                )
                
                // Two texts and a button overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = ad.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ad.subtitle,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("SHOP NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
        
        // Pager Indicators (Dots)
        Row(
            Modifier
                .padding(top = 10.dp)
                .height(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(ads.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.Black else Color.LightGray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 7.dp else 5.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
        TextButton(
            onClick = onViewAllClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = "View all >", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun BrandsSection(brandsResult: Result<List<Brand>>, onBrandClick: (String) -> Unit) {
    when (brandsResult) {
        is Result.Success -> {
            val displayBrands = brandsResult.data
            if (displayBrands.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No brands available", color = Color.Gray)
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayBrands) { brand ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onBrandClick(brand.name) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(0.5.dp, Color(0xFFE5E7EB), CircleShape), // Very thin border like the image
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = brand.imageUrl,
                                    contentDescription = brand.name,
                                    modifier = Modifier.size(40.dp),
                                    contentScale = ContentScale.Fit,
                                    loading = {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.2.dp, color = Color.Black)
                                    },
                                    error = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = brand.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                )
                            }
                            Text(
                                text = brand.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 6.dp),
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
        is Result.Loading -> {
            Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            }
        }
        else -> {}
    }
}

@Composable
fun ProductItem(product: Product, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    // Transparent background, borderless card design as shown in Screen 1
    Column(
        modifier = modifier
            .clickable {
                Toast.makeText(context, "Clicked ${product.title}", Toast.LENGTH_SHORT).show()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F4F6)) // Light background for the image placeholder
        ) {
            SubcomposeAsyncImage(
                model = product.images.firstOrNull()?.url,
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.Black)
                    }
                }
            )
            
            // Heart icon overlay on the top-right
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                }
            }
        }
        
        Column(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)) {
            Text(
                text = product.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )
            Text(
                text = "${product.minPrice.currencyCode} ${product.minPrice.amount}",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.padding(top = 1.dp)
            )
            
            // Star rating shown inline (★ 4.6 (128))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFB800)
                )
                Spacer(modifier = Modifier.width(2.dp))
                val hash = product.title.hashCode() and 0x7FFFFFFF
                val rating = 4.0 + (hash % 10) / 10.0
                val reviews = 50 + hash % 200
                Text(
                    text = "%.1f (%d)".format(rating, reviews),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

