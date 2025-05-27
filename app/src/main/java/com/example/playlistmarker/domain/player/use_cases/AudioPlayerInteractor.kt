package com.example.playlistmarker.domain.player.use_cases

import com.example.playlistmarker.domain.player.use_cases.state.UiAudioPlayerState
import com.example.playlistmarker.ui.audioplayer.viewmodel.AudioPlayerCallback
import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface AudioPlayerInteractor {
    fun setCallback(callback: AudioPlayerCallback)
    fun preparePlayer(track: TrackInfoDetails)
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun seekTo(position: Int)
    fun getPlayerState(): UiAudioPlayerState
    fun getCurrentPositionPlayer(): Int
}