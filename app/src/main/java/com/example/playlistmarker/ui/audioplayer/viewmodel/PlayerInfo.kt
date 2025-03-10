package com.example.playlistmarker.ui.audioplayer.viewmodel

import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

data class PlayerInfo (
    val playerState: UiAudioPlayerState,
    val currentTime: String,
    val currentTrack: TrackInfoDetails,
    val savedPosition: Int
)