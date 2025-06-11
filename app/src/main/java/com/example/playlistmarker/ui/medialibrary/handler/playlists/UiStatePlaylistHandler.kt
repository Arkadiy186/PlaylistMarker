package com.example.playlistmarker.ui.medialibrary.handler.playlists

interface UiStatePlaylistHandler {
    fun playlistsIsEmpty()
    fun setPlaceholderVisibility(
        isHidden: Boolean,
        text: String = "",
        imageRes: Int = 0,
        textRes: Int = 0
    )
}