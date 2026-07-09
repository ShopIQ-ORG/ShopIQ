package com.iti.presentation.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleManager {

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_ARABIC = "ar"

    fun currentLanguageTag(context: Context): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            return appLocales[0]?.language ?: LANGUAGE_ENGLISH
        }
        return context.resources.configuration.locales[0].language
    }

    fun setAppLanguage(activity: Activity, languageTag: String) {
        val current = currentLanguageTag(activity)
        if (current == languageTag) return

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
        activity.recreate()
    }
}