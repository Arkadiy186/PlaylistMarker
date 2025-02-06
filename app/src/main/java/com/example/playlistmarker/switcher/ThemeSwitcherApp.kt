package com.example.playlistmarker.switcher

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeSwitcherApp : Application() {

    private lateinit var sharedPreferences: SharedPreferences
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = getSharedPreferences(NAME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        applyTheme(darkTheme)
    }

    fun switchTheme(darkTheme: Boolean) {
        this.darkTheme = darkTheme
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply()
        applyTheme(darkTheme)
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        private const val NAME_PREFERENCES = "theme_prefs"
        private const val KEY_DARK_THEME = "key_for_dark_theme"
    }
}