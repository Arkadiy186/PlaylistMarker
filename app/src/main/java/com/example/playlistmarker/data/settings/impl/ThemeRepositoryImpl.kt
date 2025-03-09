package com.example.playlistmarker.data.settings.impl

import com.example.playlistmarker.data.settings.sharedpreferences.ThemePreferences
import com.example.playlistmarker.domain.settings.repository.ThemeRepository

class ThemeRepositoryImpl(private val themePreferences: ThemePreferences) : ThemeRepository {
    override fun isDarkThemeEnabled(): Boolean {
        return themePreferences.getTheme()
    }

    override fun setDarkTheme(isDark: Boolean) {
        themePreferences.setTheme(isDark)
    }
}