//
//  ThemeManager.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private var sharedPreferences: SharedPreferences? = null
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("shopiq_prefs", Context.MODE_PRIVATE)
        val isDark = sharedPreferences?.getBoolean("dark_theme", false) ?: false
        _isDarkTheme.value = isDark
        updateAppCompatTheme(isDark)
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        sharedPreferences?.edit()?.putBoolean("dark_theme", enabled)?.apply()
        updateAppCompatTheme(enabled)
    }

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        setDarkTheme(newValue)
    }

    private fun updateAppCompatTheme(isDark: Boolean) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
