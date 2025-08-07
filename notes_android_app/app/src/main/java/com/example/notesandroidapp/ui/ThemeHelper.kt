package com.example.notesandroidapp.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate

// PUBLIC_INTERFACE
object ThemeHelper {
    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
