package com.example.playlistmarker.switcher

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.domain.use_case.ThemeInteractor
import com.example.playlistmarker.presentation.presenter.ThemePresenter

class ThemeSwitcherApp : Application() {

    private val themeInteractor: ThemeInteractor by lazy { Creator.provideThemeInteractor() }
    private val themePresenter: ThemePresenter by lazy { ThemePresenter() }

    override fun onCreate() {
        super.onCreate()
        Creator.initialize(applicationContext)
        themePresenter.applyTheme(themeInteractor.getTheme())
    }

    fun switchTheme(enable: Boolean) {
        themeInteractor.toggleTheme(enable)
        themePresenter.applyTheme(themeInteractor.getTheme())
    }
}