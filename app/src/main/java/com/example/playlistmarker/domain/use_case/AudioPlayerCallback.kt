package com.example.playlistmarker.domain.use_case

interface AudioPlayerCallback {
    fun onPlayerPrepared()
    fun onPlayerCompleted()
}