package com.example.playlistmarker.domain.player.repository

interface PositionTimeRepository {
    fun getCurrentPosition(): Int
    fun saveCurrentPosition(position: Int)
}