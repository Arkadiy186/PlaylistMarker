package com.example.playlistmarker.domain.player.use_cases

interface AudioPlayerCallback {
    fun onPlayerPrepared()
    fun onPlayerCompleted()
}