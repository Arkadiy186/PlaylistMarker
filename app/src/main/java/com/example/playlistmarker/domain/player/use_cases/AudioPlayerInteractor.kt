package com.example.playlistmarker.domain.player.use_cases

import com.example.playlistmarker.ui.search.model.TrackInfoDetails

interface AudioPlayerInteractor {
    fun preparePlayer(track: TrackInfoDetails)
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(): Int
    fun setCallback(callback: AudioPlayerCallback)
    fun getCurrentPosition(): Int
}