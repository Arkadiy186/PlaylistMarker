package com.example.playlistmarker.data.repository

import com.example.playlistmarker.data.sharedpreferences.ThemePreferences
import com.example.playlistmarker.domain.repository.ThemeRepository

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences) : ThemeRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return themePreferences.getTheme()
    }

    override fun setDarkTheme(isDark: Boolean) {
        themePreferences.setTheme(isDark)
    }
}