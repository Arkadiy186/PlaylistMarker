package com.example.playlistmarker.domain.use_case

interface ThemeInteractor {
    fun getTheme(): Boolean
    fun toggleTheme(enable: Boolean): Boolean
}