package com.example.playlistmarker.domain.use_case

import com.example.playlistmarker.presentation.model.TrackInfoDetails

interface AudioPlayerInteractor {
    fun preparePlayer(track: TrackInfoDetails)
    fun startPlayer()
    fun pausePlayer()
    fun getPlayerState(): Int
    fun setCallback(callback: AudioPlayerCallback)
}