package com.example.playlistmarker.data.player.impl

import com.example.playlistmarker.data.player.sharedpreferences.PositionTime
import com.example.playlistmarker.domain.player.repository.PositionTimeRepository

class PositionTimeRepositoryImpl(private val positionTime: PositionTime) : PositionTimeRepository {
    override fun getCurrentPosition(): Int {
        return positionTime.getCurrentPosition()
    }

    override fun saveCurrentPosition(position: Int) {
        positionTime.saveCurrentPosition(position)
    }

    override fun resetPosition() {
        positionTime.resetPosition()
    }
}