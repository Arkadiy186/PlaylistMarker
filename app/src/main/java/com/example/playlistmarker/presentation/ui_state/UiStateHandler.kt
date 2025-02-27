package com.example.playlistmarker.presentation.ui_state

interface UiStateHandler {
    fun showLoading(isLoading: Boolean)
    fun showErrorInternet(message: Int)
    fun showNotFound()
    fun placeholderSetVisibility(isHidden: Boolean, text: String = "", imageRes: Int = 0, textRes: Int = 0)
}