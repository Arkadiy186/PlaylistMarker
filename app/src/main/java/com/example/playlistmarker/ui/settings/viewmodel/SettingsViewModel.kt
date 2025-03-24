package com.example.playlistmarker.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmarker.domain.settings.use_cases.ThemeInteractor

class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {


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