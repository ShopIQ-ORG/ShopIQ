package com.iti.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.iti.domain.repositories.settings.SettingsRepository
import com.iti.presentation.navigation.AppNavigation
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.presentation.util.CurrencyManager
import com.iti.presentation.util.ThemeManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : androidx.appcompat.app.AppCompatActivity() {
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.initialize(applicationContext)
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sync local CurrencyManager with stored DB currency state
        lifecycleScope.launch {
            settingsRepository.getSelectedCurrency().collect { currency ->
                CurrencyManager.updateSelectedCurrency(currency)
            }
        }
        lifecycleScope.launch {
            settingsRepository.getPopularCurrencies().collect { currencies ->
                CurrencyManager.updateSupportedCurrencies(currencies)
            }
        }

        setContent {
            val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()
            ShopIQTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }
}