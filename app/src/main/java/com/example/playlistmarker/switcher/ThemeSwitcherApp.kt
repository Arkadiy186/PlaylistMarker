package com.example.playlistmarker.switcher

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class ThemeSwitcherApp : Application() {

    companion object {
        private const val NAME_PREFERENCES = "theme_prefs"
        private const val KEY_DARK_THEME = "key_for_dark_theme"
    }

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(NAME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        setAndApplyTheme(darkTheme)
    }

    fun switchTheme(darkTheme : Boolean) {
        this.darkTheme = darkTheme

        val sharedPreferences = getSharedPreferences(NAME_PREFERENCES, MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply()
        setAndApplyTheme(darkTheme)
    }

    private fun setAndApplyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}