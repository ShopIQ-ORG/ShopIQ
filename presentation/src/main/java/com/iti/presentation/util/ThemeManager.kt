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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private var sharedPreferences: SharedPreferences? = null
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("shopiq_prefs", Context.MODE_PRIVATE)
        _isDarkTheme.value = sharedPreferences?.getBoolean("dark_theme", false) ?: false
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        sharedPreferences?.edit()?.putBoolean("dark_theme", enabled)?.apply()
    }

    fun toggleTheme() {
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        sharedPreferences?.edit()?.putBoolean("dark_theme", newValue)?.apply()
    }
}
