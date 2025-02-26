package com.example.playlistmarker.presentation.presenter

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarker.domain.use_case.ThemeInteractor

class SettingsPresenter(private val themeInteractor: ThemeInteractor) {
    private var view: SettingsThemeView? = null

    fun attachView(view: SettingsThemeView) {
        this.view = view
        val isDarkTheme = themeInteractor.getTheme()
        view.setThemeState(isDarkTheme)
    }

    fun detachView() {
        this.view = null
    }

    fun onThemeSwitchClicked(isChecked: Boolean) {
        themeInteractor.toggleTheme(isChecked)
        applyTheme(isChecked)
        view?.setThemeState(isChecked)
    }

    private fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}