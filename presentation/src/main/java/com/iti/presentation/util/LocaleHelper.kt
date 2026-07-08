package com.iti.presentation.util

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object LocaleHelper {
    fun getCurrentLanguage(): String {
        return AppCompatDelegate.getApplicationLocales()[0]?.language 
            ?: Locale.getDefault().language
    }

    fun isArabic(): Boolean {
        return getCurrentLanguage() == "ar"
    }
}
