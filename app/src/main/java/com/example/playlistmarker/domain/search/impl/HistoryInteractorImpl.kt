package com.example.playlistmarker.domain.search.impl

import com.example.playlistmarker.domain.search.model.Track
import com.example.playlistmarker.domain.search.repository.HistoryRepository
import com.example.playlistmarker.domain.search.use_cases.HistoryInteractor

class HistoryInteractorImpl(private val historyRepository: HistoryRepository) : HistoryInteractor {

    override fun addTrackHistory(track: Track) {
        historyRepository.addTrackHistory(track)
    }

    override suspend fun getTrackHistory(): List<Track> {
        return historyRepository.getHistory()
    }

    override fun clearHistory() {
        return historyRepository.clearHistory()
    }

    override suspend fun loadHistory(consumer: (List<Track>) -> Unit) {
        val history = getTrackHistory()
        consumer(history)
    }
}