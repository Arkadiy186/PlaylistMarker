package com.example.playlistmarker.ui.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmarker.creator.Creator
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themeInteractor: ThemeInteractor by lazy { Creator.provideThemeInteractor() }

    private val _uiThemeLiveData = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _uiThemeLiveData

    init {
        _uiThemeLiveData.postValue(themeInteractor.getTheme())
    }

    fun onThemeSwitchClicked(isChecked: Boolean) {
        themeInteractor.toggleTheme(isChecked)
        _uiThemeLiveData.postValue(isChecked)
    }
}