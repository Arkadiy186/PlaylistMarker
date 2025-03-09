package com.example.playlistmarker.ui.audioplayer.viewmodel

import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState

interface AudioPlayerCallback {
    fun onPlayerStateChanged(state: UiAudioPlayerState)
    fun onTrackCompletion()
}