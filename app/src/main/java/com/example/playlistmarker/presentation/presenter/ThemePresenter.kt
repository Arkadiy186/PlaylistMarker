package com.example.playlistmarker.presentation.presenter

import androidx.appcompat.app.AppCompatDelegate

class ThemePresenter {
    fun applyTheme(darkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}