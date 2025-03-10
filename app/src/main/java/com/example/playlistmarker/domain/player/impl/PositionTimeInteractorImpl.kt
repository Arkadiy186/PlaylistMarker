package com.example.playlistmarker.domain.player.impl

import com.example.playlistmarker.domain.player.repository.PositionTimeRepository
import com.example.playlistmarker.domain.player.use_cases.PositionTimeInteractor

class PositionTimeInteractorImpl(private val positionTimeRepository: PositionTimeRepository) : PositionTimeInteractor {
    override fun getCurrentPosition(): Int {
        return positionTimeRepository.getCurrentPosition()
    }

    override fun saveCurrentPosition(position: Int) {
        positionTimeRepository.saveCurrentPosition(position)
    }
    override fun resetPosition() {
        positionTimeRepository.resetPosition()
    }
}