package com.iti.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.iti.presentation.navigation.AppNavigation
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.ThemeManager

class MainActivity : androidx.appcompat.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.initialize(applicationContext)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()
            ShopIQTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}