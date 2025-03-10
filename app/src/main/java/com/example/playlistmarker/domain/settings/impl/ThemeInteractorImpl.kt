package com.example.playlistmarker.domain.settings.impl

import com.example.playlistmarker.domain.settings.repository.ThemeRepository
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun getTheme(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun toggleTheme(enable: Boolean): Boolean {
        themeRepository.setDarkTheme(enable)
        return enable
    }
}