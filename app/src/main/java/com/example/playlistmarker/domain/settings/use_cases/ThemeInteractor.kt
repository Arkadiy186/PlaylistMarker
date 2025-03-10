package com.example.playlistmarker.domain.settings.use_cases

interface ThemeInteractor {
    fun getTheme(): Boolean
    fun toggleTheme(enable: Boolean): Boolean
}