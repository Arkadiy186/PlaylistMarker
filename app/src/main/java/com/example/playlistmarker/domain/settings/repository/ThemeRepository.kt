package com.example.playlistmarker.domain.settings.repository

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(isDark: Boolean)
}