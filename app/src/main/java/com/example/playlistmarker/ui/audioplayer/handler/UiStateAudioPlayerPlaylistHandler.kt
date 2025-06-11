package com.example.playlistmarker.ui.audioplayer.handler

interface UiStateAudioPlayerPlaylistHandler {
    fun setPlaceholderVisibility(
        isHidden: Boolean,
        text: String = "",
        imageRes: Int = 0,
        textRes: Int = 0
    )
}