package com.example.playlistmarker.domain.player.use_cases

interface PositionTimeInteractor {
    fun getCurrentPosition(): Int
    fun saveCurrentPosition(position: Int)
    fun resetPosition()
}