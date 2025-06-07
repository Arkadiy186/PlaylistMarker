package com.example.playlistmarker.ui.medialibrary.handler.favouritetracks

interface UiStateFavouriteHandler {
    fun favouriteNotFound()
    fun placeholderSetVisibility(
        isHidden: Boolean,
        text: String = "",
        imageRes: Int = 0,
        textRes: Int = 0)
}