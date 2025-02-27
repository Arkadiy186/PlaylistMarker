package com.example.playlistmarker.data.sharedpreferences

import android.content.SharedPreferences

class ThemePreferences(private val sharedPreferences: SharedPreferences) {

    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)

    }

    fun setTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
    }

    companion object {
        private const val KEY_DARK_THEME = "key_for_dark_theme"
    }
}