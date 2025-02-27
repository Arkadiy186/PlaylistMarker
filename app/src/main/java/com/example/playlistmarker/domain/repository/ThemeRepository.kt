package com.example.playlistmarker.domain.repository

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkTheme(isDark: Boolean)
}