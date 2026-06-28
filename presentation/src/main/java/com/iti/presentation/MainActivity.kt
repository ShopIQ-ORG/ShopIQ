package com.iti.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.iti.presentation.productdetails.ProductDetailsScreen
import com.iti.presentation.ui.theme.ShopIQTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ShopIQTheme {
                ProductDetailsScreen(
                    productId = 9746399428843L,
                    onBackClick = { finish() }
                )
            }
        }
    }
}