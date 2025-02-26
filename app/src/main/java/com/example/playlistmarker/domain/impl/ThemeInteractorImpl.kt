package com.example.playlistmarker.domain.impl

import com.example.playlistmarker.domain.repository.ThemeRepository
import com.example.playlistmarker.domain.use_case.ThemeInteractor

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun getTheme(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun toggleTheme(enable: Boolean): Boolean {
        themeRepository.setDarkTheme(enable)
        return enable
    }
}